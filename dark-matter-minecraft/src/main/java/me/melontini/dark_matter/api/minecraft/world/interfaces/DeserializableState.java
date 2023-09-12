package me.melontini.dark_matter.api.minecraft.world.interfaces;

import net.minecraft.nbt.NbtCompound;

public interface DeserializableState {
    void readNbt(NbtCompound nbt);
}
