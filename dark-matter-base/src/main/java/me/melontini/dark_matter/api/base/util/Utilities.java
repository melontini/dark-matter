package me.melontini.dark_matter.api.base.util;

import lombok.SneakyThrows;
import me.melontini.dark_matter.api.base.util.classes.ThrowingConsumer;
import me.melontini.dark_matter.api.base.util.classes.ThrowingFunction;
import me.melontini.dark_matter.api.base.util.classes.ThrowingRunnable;
import me.melontini.dark_matter.api.base.util.classes.ThrowingSupplier;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.*;

public final class Utilities {
    private Utilities() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public static boolean IS_DEV = FabricLoader.getInstance().isDevelopmentEnvironment();

    @Deprecated
    public static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    @Deprecated
    public static final Random RANDOM = MathStuff.random();

    private static final BooleanSupplier TRUTH = () -> true;
    private static final BooleanSupplier FALSE = () -> false;

    public static <T> T pickAtRandom(@NotNull T[] list) {
        MakeSure.notEmpty(list);
        return list[RANDOM.nextInt(list.length)];
    }

    public static <T> T pickAtRandom(@NotNull List<T> list) {
        MakeSure.notEmpty(list);
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static BooleanSupplier getTruth() {
        return TRUTH;
    }

    public static BooleanSupplier getFalse() {
        return FALSE;
    }

    public static <F, U> U cast(F o) {
        return (U) o;
    }

    @SneakyThrows
    public static <E extends Throwable> void runUnchecked(ThrowingRunnable<E> runnable) {
        runnable.run();
    }

    @SneakyThrows
    public static <T, E extends Throwable> T supplyUnchecked(ThrowingSupplier<T, E> supplier) {
        return supplier.get();
    }

    @SneakyThrows
    public static <T, E extends Throwable> T consumeUnchecked(T obj, ThrowingConsumer<T, E> consumer) {
        consumer.accept(obj);
        return obj;
    }

    @SneakyThrows
    public static <T, R, E extends Throwable> R processUnchecked(T obj, ThrowingFunction<T, R, E> function) {
        return function.apply(obj);
    }

    public static void run(Runnable runnable) {
        runnable.run();
    }

    public static <T> T supply(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T consume(T obj, Consumer<T> consumer) {
        consumer.accept(obj);
        return obj;
    }

    public static <T, U> T consume(T obj, U obj1, BiConsumer<T, U> consumer) {
        consumer.accept(obj, obj1);
        return obj;
    }

    public static <T, R> R process(T obj, Function<T, R> function) {
        return function.apply(obj);
    }

    public static <T, U, R> R process(T obj, U obj1, BiFunction<T, U, R> function) {
        return function.apply(obj, obj1);
    }

    public static <T> boolean test(T obj, Predicate<T> test) {
        return test.test(obj);
    }

    public static <T, U> boolean test(T obj, U obj1, BiPredicate<T, U> test) {
        return test.test(obj, obj1);
    }

    public static boolean isDev() {
        return IS_DEV;
    }

    public static String getCallerName() {
        return getCallerName(3);
    }

    public static String getCallerName(int depth) {
        return STACK_WALKER.walk(s -> {
            var first = s.skip(depth).findFirst().orElse(null);
            return first != null ? first.getClassName() + "#" + first.getMethodName() : "NoClassNameFound";
        });
    }

    public static Class<?> getCallerClass() {
        return getCallerClass(3);
    }

    public static Class<?> getCallerClass(int depth) {
        return STACK_WALKER.walk(s -> {
            var first = s.skip(depth).findFirst().orElse(null);
            return first != null ? first.getDeclaringClass() : null;
        });
    }
}
