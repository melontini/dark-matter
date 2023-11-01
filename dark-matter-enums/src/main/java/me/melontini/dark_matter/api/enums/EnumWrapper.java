package me.melontini.dark_matter.api.enums;

import me.melontini.dark_matter.api.base.util.MakeSure;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.raid.Raid;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

@SuppressWarnings("unused")
public class EnumWrapper {
    private EnumWrapper() {
        throw new UnsupportedOperationException();
    }
    public static class AbstractMinecartEntityType {
        private AbstractMinecartEntityType() {
            throw new UnsupportedOperationException();
        }
        public static AbstractMinecartEntity.Type extend(String internalName) {
            MakeSure.notEmpty(internalName, "Tried to extend AbstractMinecartEntity.Type with an empty internalName");

            return AbstractMinecartEntity.Type.values()[0].dark_matter$extend(internalName);
        }
    }

    public static class BoatEntityType {
        private BoatEntityType() {
            throw new UnsupportedOperationException();
        }
        public static BoatEntity.Type extend(String internalName, Block base, String name) {
            MakeSure.notEmpty(internalName, "Tried to extend BoatEntity.Type with an empty internalName");
            MakeSure.notNull(base, "Tried to extend BoatEntity.Type with a null block");
            MakeSure.notEmpty(name, "Tried to extend BoatEntity.Type with an empty name");

            return BoatEntity.Type.values()[0].dark_matter$extend(internalName, base, name);
        }
    }

    public static class EnchantmentTarget {
        private EnchantmentTarget() {
            throw new UnsupportedOperationException();
        }
        public static net.minecraft.enchantment.EnchantmentTarget extend(String internalName, Predicate<Item> predicate) {
            MakeSure.notEmpty(internalName, "Tried to extend EnchantmentTarget with an empty internalName");
            MakeSure.notNull(predicate, "Tried to extend EnchantmentTarget with a null predicate");

            return net.minecraft.enchantment.EnchantmentTarget.values()[0].dark_matter$extend(internalName, predicate);
        }
    }

    public static class Formatting {
        private Formatting() {
            throw new UnsupportedOperationException();
        }
        public static net.minecraft.util.Formatting extend(String internalName, String name, Character code, Boolean modifier, Integer colorIndex, @Nullable Integer colorValue) {
            MakeSure.notEmpty(internalName, "Tried to extend Formatting with an empty internalName");
            MakeSure.notNulls(code, modifier, colorIndex, "Tried to extend Formatting with a null argument");
            MakeSure.notEmpty(name, "Tried to extend Formatting with an empty name");

            return net.minecraft.util.Formatting.values()[0].dark_matter$extend(internalName, name, code, modifier, colorIndex, colorValue);
        }

        public static net.minecraft.util.Formatting extend(String internalName, String name, Character code, Integer colorIndex, @Nullable Integer colorValue) {
            MakeSure.notEmpty(internalName, "Tried to extend Formatting with an empty internalName");
            MakeSure.notNulls(code, colorIndex, "Tried to extend Formatting with a null argument");
            MakeSure.notEmpty(name, "Tried to extend Formatting with an empty name");

            return net.minecraft.util.Formatting.values()[0].dark_matter$extend(internalName, name, code, colorIndex, colorValue);
        }

        public static net.minecraft.util.Formatting extend(String internalName, String name, Character code, Boolean modifier) {
            MakeSure.notEmpty(internalName, "Tried to extend Formatting with an empty internalName");
            MakeSure.notNulls(code, modifier, "Tried to extend Formatting with a null argument");
            MakeSure.notEmpty(name, "Tried to extend Formatting with an empty name");

            return net.minecraft.util.Formatting.values()[0].dark_matter$extend(internalName, name, code, modifier);
        }
    }

    public static class Rarity {
        private Rarity() {
            throw new UnsupportedOperationException();
        }
        public static net.minecraft.util.Rarity extend(String internalName, net.minecraft.util.Formatting formatting) {
            MakeSure.notEmpty(internalName, "Tried to extend Rarity with an empty internalName");
            MakeSure.notNull(formatting, "Tried to extend Rarity with a null formatting");

            return net.minecraft.util.Rarity.values()[0].dark_matter$extend(internalName, formatting);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class RecipeBookGroup {
        private RecipeBookGroup() {
            throw new UnsupportedOperationException();
        }
        public static net.minecraft.client.recipebook.RecipeBookGroup extend(String internalName, ItemStack... stacks) {
            MakeSure.notEmpty(internalName, "Tried to extend RecipeBookGroup with an empty internalName");

            return net.minecraft.client.recipebook.RecipeBookGroup.values()[0].dark_matter$extend(internalName, (Object) stacks);
        }
    }

    public static class RaidMember {
        private RaidMember() {
            throw new UnsupportedOperationException();
        }
        public static Raid.Member extend(String internalName, EntityType<? extends RaiderEntity> type, int[] countInWave) {
            MakeSure.notEmpty(internalName, "Tried to extend Raid.Member with an empty internalName");
            MakeSure.isTrue(countInWave.length > 0, "Tried to extend Raid.Member with empty countInWave array");

            return Raid.Member.values()[0].dark_matter$extend(internalName, type, countInWave);
        }
    }
}
