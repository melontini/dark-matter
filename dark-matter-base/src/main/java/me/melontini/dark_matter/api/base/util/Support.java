package me.melontini.dark_matter.api.base.util;

import me.melontini.dark_matter.api.base.util.classes.ThrowingRunnable;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Support {

    public static void run(String modId, Supplier<Runnable> runnable) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            runnable.get().run();
        }
    }

    public static <T> Optional<T> get(String modId, Supplier<Supplier<T>> supplier) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            return Optional.ofNullable(supplier.get().get());
        }
        return Optional.empty();
    }

    public static void runWeak(String modId, Supplier<ThrowingRunnable> runnable) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            try {
                runnable.get().run();
            } catch (Throwable ignored) {
                // ignored
            }
        }
    }

    public static <T> Optional<T> getWeak(String modId, Supplier<Callable<T>> supplier) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            try {
                return Optional.ofNullable(supplier.get().call());
            } catch (Throwable e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }


    public static void run(EnvType envType, Supplier<Runnable> runnable) {
        if (FabricLoader.getInstance().getEnvironmentType() == envType) {
            runnable.get().run();
        }
    }

    public static <T> Optional<T> get(EnvType envType, Supplier<Supplier<T>> supplier) {
        if (FabricLoader.getInstance().getEnvironmentType() == envType) {
            return Optional.ofNullable(supplier.get().get());
        }
        return Optional.empty();
    }

    public static void runWeak(EnvType envType, Supplier<ThrowingRunnable> runnable) {
        if (FabricLoader.getInstance().getEnvironmentType() == envType) {
            try {
                runnable.get().run();
            } catch (Throwable ignored) {
                // ignored
            }
        }
    }

    public static <T> Optional<T> getWeak(EnvType envType, Supplier<Callable<T>> supplier) {
        if (FabricLoader.getInstance().getEnvironmentType() == envType) {
            try {
                return Optional.ofNullable(supplier.get().call());
            } catch (Throwable e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }


    public static void runEnv(Supplier<Runnable> clientRunnable, Supplier<Runnable> serverRunnable) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            clientRunnable.get().run();
        } else {
            serverRunnable.get().run();
        }
    }

    public static <T> Optional<T> getEnv(Supplier<Supplier<T>> clientSupplier, Supplier<Supplier<T>> serverSupplier) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return Optional.ofNullable(clientSupplier.get().get());
        } else {
            return Optional.ofNullable(serverSupplier.get().get());
        }
    }

    public static void runEnvWeak(Supplier<ThrowingRunnable> clientRunnable, Supplier<ThrowingRunnable> serverRunnable) {
        try {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                clientRunnable.get().run();
            } else {
                serverRunnable.get().run();
            }
        } catch (Throwable ignored) {
            // ignored
        }
    }

    public static <T> Optional<T> getEnvWeak(Supplier<Callable<T>> clientSupplier, Supplier<Callable<T>> serverSupplier) {
        try {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                return Optional.ofNullable(clientSupplier.get().call());
            } else {
                return Optional.ofNullable(serverSupplier.get().call());
            }
        } catch (Throwable e) {
            return Optional.empty();
        }
    }
}
