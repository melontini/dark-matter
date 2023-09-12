package me.melontini.dark_matter.api.minecraft.world;

import me.melontini.dark_matter.api.minecraft.world.interfaces.DeserializableState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Experimental
public final class PersistentStateHelper {

    public static <T extends PersistentState> T getOrCreate(ServerWorld world, Function<NbtCompound, T> readFunction, Supplier<T> supplier, String id) {
        return world.getPersistentStateManager().getOrCreate(readFunction, supplier, id);
    }

    public static <T extends PersistentState> T getOrCreate(ServerWorld world, Supplier<T> supplier, String id) {
        return getOrCreate(world, nbt -> {
            T state = supplier.get();
            if (state instanceof DeserializableState ds) {
                ds.readNbt(nbt);
            } else throw new IllegalArgumentException("State must implement DeserializableState to use getOrCreate(ServerWorld, Supplier<T>, String)!");
            return state;
        }, supplier, id);
    }

    public static boolean isStateLoaded(ServerWorld world, String id) {
        return world.getPersistentStateManager().loadedStates.containsKey(id);
    }

    public static <T extends PersistentState> void consumeIfLoaded(ServerWorld world, String id, BiFunction<ServerWorld, String, T> getFunc, Consumer<T> action) {
        if (isStateLoaded(world, id)) {
            action.accept(getFunc.apply(world, id));
        }
    }

    public static <T extends PersistentState, R> Optional<R> processIfLoaded(ServerWorld world, String id, BiFunction<ServerWorld, String, T> getFunc, Function<T, R> action) {
        if (isStateLoaded(world, id)) {
            return Optional.ofNullable(action.apply(getFunc.apply(world, id)));
        }
        return Optional.empty();
    }
}
