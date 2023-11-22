package me.melontini.dark_matter.api.minecraft.data;

import lombok.experimental.UtilityClass;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
@SuppressWarnings("unused")
public class NbtUtil {

    public static @NotNull NbtCompound writeInventoryToNbt(NbtCompound nbt, @NotNull Inventory inventory) {
        return writeInventoryToNbt("Items", nbt, inventory);
    }

    /**
     * Writes items in an inventory to NbtCompound.
     *
     * @param nbt       the NbtCompound to write the inventory to
     * @param inventory the inventory to write to the NbtCompound
     * @return the NbtCompound with the inventory data written to it
     */
    public static @NotNull NbtCompound writeInventoryToNbt(String key, NbtCompound nbt, @NotNull Inventory inventory) {
        nbt = (nbt == null) ? new NbtCompound() : nbt;
        NbtList nbtList = new NbtList();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                nbtList.add(itemStack.writeNbt(NbtBuilder.create().putByte("Slot", (byte) i).build()));
            }
        }
        nbt.put(key, nbtList);
        return nbt;
    }

    public static void readInventoryFromNbt(NbtCompound nbt, Inventory inventory) {
        readInventoryFromNbt("Items", nbt, inventory);
    }

    /**
     * Reads items in an inventory from a NbtCompound.
     *
     * @param nbt       the NbtCompound to read the inventory from
     * @param inventory the inventory to read the data into
     */
    public static void readInventoryFromNbt(String key, NbtCompound nbt, Inventory inventory) {
        if (nbt != null)
            if (nbt.getList(key, NbtElement.COMPOUND_TYPE) != null) {
                NbtList nbtList = nbt.getList(key, NbtElement.COMPOUND_TYPE);
                for (int i = 0; i < nbtList.size(); ++i) {
                    NbtCompound nbtCompound = nbtList.getCompound(i);
                    int j = nbtCompound.getByte("Slot") & 255;
                    //noinspection ConstantConditions
                    if (j >= 0 && j < inventory.size()) {
                        inventory.setStack(j, ItemStack.fromNbt(nbtCompound));
                    }
                }
            }
    }

    @Contract("null, _, _ -> param3")
    public static int getInt(NbtCompound nbt, String name, int defaultValue) {
        if (nbt == null) return defaultValue;
        if (!nbt.contains(name)) return defaultValue;
        return nbt.getInt(name);
    }

    @Contract("null, _, _, _ -> param3")
    public static int getInt(NbtCompound nbt, String name, int min, int max) {
        if (nbt == null) return min;
        if (!nbt.contains(name)) return min;
        int i = nbt.getInt(name);
        return MathHelper.clamp(i, min, max);
    }

    @Contract("null, _, _ -> param3")
    public static float getFloat(NbtCompound nbt, String name, float defaultValue) {
        if (nbt == null) return defaultValue;
        if (!nbt.contains(name)) return defaultValue;
        return nbt.getFloat(name);
    }

    @Contract("null, _, _, _ -> param3")
    public static float getFloat(NbtCompound nbt, String name, float min, float max) {
        if (nbt == null) return min;
        if (!nbt.contains(name)) return min;
        float i = nbt.getFloat(name);
        return MathHelper.clamp(i, min, max);
    }

    @Contract("null, _, _ -> param3")
    public static double getDouble(NbtCompound nbt, String name, double defaultValue) {
        if (nbt == null) return defaultValue;
        if (!nbt.contains(name)) return defaultValue;
        return nbt.getDouble(name);
    }

    @Contract("null, _, _, _ -> param3")
    public static double getDouble(NbtCompound nbt, String name, double min, double max) {
        if (nbt == null) return min;
        if (!nbt.contains(name)) return min;
        double i = nbt.getDouble(name);
        return MathHelper.clamp(i, min, max);
    }

    @Contract("null, _, _ -> param3")
    public static byte getByte(NbtCompound nbt, String name, byte defaultValue) {
        if (nbt == null) return defaultValue;
        if (!nbt.contains(name)) return defaultValue;
        return nbt.getByte(name);
    }

    @Contract("null, _, _, _ -> param3")
    public static float getByte(NbtCompound nbt, String name, byte min, byte max) {
        if (nbt == null) return min;
        if (!nbt.contains(name)) return min;
        byte i = nbt.getByte(name);
        return MathHelper.clamp(i, min, max);
    }


    @Contract("null, _, _ -> param3")
    public static String getString(NbtCompound nbt, String name, String defaultValue) {
        if (nbt == null) return defaultValue;
        if (!nbt.contains(name)) return defaultValue;
        return nbt.getString(name);
    }
}
