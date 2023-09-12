package me.melontini.dark_matter.api.base.reflect;

import me.melontini.dark_matter.impl.base.reflect.ReflectionInternals;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Reflect {

    //
    // find members
    //

    public static <T> Optional<Constructor<T>> findConstructor(@NotNull Class<T> clazz, Object... args) {
        return Optional.ofNullable(ReflectionInternals.findConstructor(clazz, Arrays.stream(args).toList()));
    }

    public static <T> Optional<Constructor<T>> findConstructor(@NotNull Class<T> clazz, List<Object> args) {
        return Optional.ofNullable(ReflectionInternals.findConstructor(clazz, args));
    }

    public static Optional<Method> findMethod(@NotNull Class<?> clazz, String name, Object... args) {
        return Optional.ofNullable(ReflectionInternals.findMethod(clazz, name, Arrays.stream(args).toList()));
    }

    public static Optional<Method> findMethod(@NotNull Class<?> clazz, String name, List<Object> args) {
        return Optional.ofNullable(ReflectionInternals.findMethod(clazz, name, args));
    }

    public static Optional<Field> findField(@NotNull Class<?> clazz, String name) {
        return Optional.ofNullable(ReflectionInternals.findField(clazz, name));
    }

    //
    // process if present
    //

    public static <T, R> Optional<R> processConstructor(Function<Constructor<T>, R> func, @NotNull Class<T> cls, Object... args) {
        return findConstructor(cls, args).map(func);
    }

    public static <T, R> Optional<R> processConstructor(Function<Constructor<T>, R> func, @NotNull Class<T> cls, List<Object> args) {
        return findConstructor(cls, args).map(func);
    }

    public static <T, R> Optional<R> processMethod(Function<Method, R> func, @NotNull Class<?> cls, String name, Object... args) {
        return findMethod(cls, name, args).map(func);
    }

    public static <T, R> Optional<R> processMethod(Function<Method, R> func, @NotNull Class<?> cls, String name, List<Object> args) {
        return findMethod(cls, name, args).map(func);
    }

    public static <T, R> Optional<R> processField(Function<Field, R> func, @NotNull Class<?> cls, String name) {
        return findField(cls, name).map(func);
    }

    //
    // consume if present
    //

    public static <T> void consumeConstructor(Consumer<Constructor<T>> consumer, @NotNull Class<T> cls, Object... args) {
        findConstructor(cls, args).ifPresent(consumer);
    }

    public static <T> void consumeConstructor(Consumer<Constructor<T>> consumer, @NotNull Class<T> cls, List<Object> args) {
        findConstructor(cls, args).ifPresent(consumer);
    }

    public static void consumeMethod(Consumer<Method> consumer, @NotNull Class<?> cls, String name, Object... args) {
        findMethod(cls, name, args).ifPresent(consumer);
    }

    public static void consumeMethod(Consumer<Method> consumer, @NotNull Class<?> cls, String name, List<Object> args) {
        findMethod(cls, name, args).ifPresent(consumer);
    }

    public static void consumeField(Consumer<Field> consumer, @NotNull Class<?> cls, String name) {
        findField(cls, name).ifPresent(consumer);
    }

    //
    // set accessible
    //

    public static <T extends AccessibleObject> T setAccessible(T member) {
        return ReflectionInternals.setAccessible(member, true);
    }

    public static <T extends AccessibleObject> T setAccessible(T member, boolean set) {
        return ReflectionInternals.setAccessible(member, set);
    }
}
