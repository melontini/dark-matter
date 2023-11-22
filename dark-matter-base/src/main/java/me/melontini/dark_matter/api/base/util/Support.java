package me.melontini.dark_matter.api.base.util;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.classes.ThrowingRunnable;
import me.melontini.dark_matter.api.base.util.classes.ThrowingSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@UtilityClass
public class Support {

    public static EnvType environment() {
        return FabricLoader.getInstance().getEnvironmentType();
    }

    public static void run(String modId, Supplier<Runnable> runnable) {
        if (FabricLoader.getInstance().isModLoaded(modId)) runnable.get().run();
    }

    public static <T> Optional<T> get(String modId, Supplier<Supplier<T>> supplier) {
        if (FabricLoader.getInstance().isModLoaded(modId)) return Optional.ofNullable(supplier.get().get());
        return Optional.empty();
    }

    public static <E extends Throwable> void runWeak(String modId, Supplier<ThrowingRunnable<E>> runnable) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            try {
                runnable.get().run();
            } catch (Throwable ignored) {
                // ignored
            }
        }
    }

    public static <T, E extends Throwable> Optional<T> getWeak(String modId, Supplier<ThrowingSupplier<T, E>> supplier) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            try {
                return Optional.ofNullable(supplier.get().get());
            } catch (Throwable e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }


    public static void run(EnvType envType, Supplier<Runnable> runnable) {
        if (environment() == envType) runnable.get().run();
    }

    public static <T> Optional<T> get(EnvType envType, Supplier<Supplier<T>> supplier) {
        if (environment() == envType) return Optional.ofNullable(supplier.get().get());
        return Optional.empty();
    }

    public static <E extends Throwable> void runWeak(EnvType envType, Supplier<ThrowingRunnable<E>> runnable) {
        if (environment() == envType) {
            try {
                runnable.get().run();
            } catch (Throwable ignored) {
                // ignored
            }
        }
    }

    public static <T, E extends Throwable> Optional<T> getWeak(EnvType envType, Supplier<ThrowingSupplier<T, E>> supplier) {
        if (environment() == envType) {
            try {
                return Optional.ofNullable(supplier.get().get());
            } catch (Throwable e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }


    public static void runEnv(Supplier<Runnable> clientRunnable, Supplier<Runnable> serverRunnable) {
        if (environment() == EnvType.CLIENT) clientRunnable.get().run();
        else serverRunnable.get().run();
    }

    public static <T> Optional<T> getEnv(Supplier<Supplier<T>> clientSupplier, Supplier<Supplier<T>> serverSupplier) {
        if (environment() == EnvType.CLIENT) return Optional.ofNullable(clientSupplier.get().get());
        else return Optional.ofNullable(serverSupplier.get().get());
    }

    public static <E extends Throwable> void runEnvWeak(Supplier<ThrowingRunnable<E>> clientRunnable, Supplier<ThrowingRunnable<E>> serverRunnable) {
        try {
            if (environment() == EnvType.CLIENT) clientRunnable.get().run();
            else serverRunnable.get().run();
        } catch (Throwable ignored) {
            // ignored
        }
    }

    public static <T, E extends Throwable> Optional<T> getEnvWeak(Supplier<ThrowingSupplier<T, E>> clientSupplier, Supplier<ThrowingSupplier<T, E>> serverSupplier) {
        try {
            if (environment() == EnvType.CLIENT) return Optional.ofNullable(clientSupplier.get().get());
            else return Optional.ofNullable(serverSupplier.get().get());
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    public static <T> void whenAvailable(String id, Class<T> type /*generics moment*/, BiConsumer<String, T> consumer) {
        FabricLoader.getInstance().getObjectShare().whenAvailable(id, (s, o) -> {
            if (type.isInstance(o)) consumer.accept(s, type.cast(o));
        });
    }

    public static <T> void whenAvailable(EnvType envType, String id, Class<T> type, BiConsumer<String, T> consumer) {
        if (environment() == envType) FabricLoader.getInstance().getObjectShare().whenAvailable(id, (s, o) -> {
            if (type.isInstance(o)) consumer.accept(s, type.cast(o));
        });
    }
}
