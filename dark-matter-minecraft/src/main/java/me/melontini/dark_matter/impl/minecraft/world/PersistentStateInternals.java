package me.melontini.dark_matter.impl.minecraft.world;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PersistentStateInternals {

    public static final ThreadLocal<Boolean> CANCEL_UPDATE = ThreadLocal.withInitial(() -> false);

    public static <T extends PersistentState> T getOrCreate(ServerWorld world, Function<NbtCompound, T> readFunction, Supplier<T> supplier, String id) {
        T t;
        try {
            CANCEL_UPDATE.set(true);
            t = world.getPersistentStateManager().getOrCreate(getType(supplier, readFunction), id);
        } finally {
            CANCEL_UPDATE.remove();
        }
        return t;
    }

    public static <T extends PersistentState> PersistentState.Type<T> getType(Supplier<T> constructor, Function<NbtCompound, T> deserializer) {
        return new PersistentState.Type<>(constructor, deserializer, DataFixTypes.SAVED_DATA_RAIDS);
    }
}
