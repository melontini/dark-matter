package me.melontini.dark_matter.api.base.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EntrypointRunner {

    public static <T> void runWithContext(String entrypoint, Class<T> type, BiConsumer<? super T, ModContainer> invoker) {
        Collection<EntrypointContainer<T>> entrypoints = FabricLoader.getInstance().getEntrypointContainers(entrypoint, type);
        if (entrypoints.isEmpty()) return;

        for (EntrypointContainer<T> container : entrypoints) {
            try {
                invoker.accept(container.getEntrypoint(), container.getProvider());
            } catch (Throwable t) {
                String message = container.getProvider().getMetadata().getContact().get("issues")
                        .map(s -> "Failed to run ['%s'] due to ['%s'] throwing an exception! Please report this issue at %s".formatted(entrypoint, container.getProvider().getMetadata().getId(), s))
                        .orElseGet(() -> "Failed to run ['%s'] due to ['%s'] throwing an exception!".formatted(entrypoint, container.getProvider().getMetadata().getId()));
                throw new RuntimeException(message, t);
            }
        }
    }

    public static <T> void runEntrypoint(String entrypoint, Class<T> type, Consumer<? super T> invoker) {
        runWithContext(entrypoint, type, (t, m) -> invoker.accept(t));
    }

    public static <T> void run(String entrypoint, Class<T> type, Consumer<? super T> invoker) {
        runWithContext(entrypoint, type, (t, m) -> invoker.accept(t));
    }
}
