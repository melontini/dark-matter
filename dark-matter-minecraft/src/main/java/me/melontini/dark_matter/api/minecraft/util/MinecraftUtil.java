package me.melontini.dark_matter.api.minecraft.util;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.MathStuff;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.Collection;

@UtilityClass
@SuppressWarnings("unused")
public class MinecraftUtil {

    public static void appendStacks(Collection<ItemStack> stacks, Collection<ItemStack> list) {
        appendStacks(stacks, list, true);
    }

    public static void appendStacks(Collection<ItemStack> stacks, Collection<ItemStack> list, boolean lineBreak) {
        if (list == null || list.isEmpty()) return; //we shouldn't add line breaks if there are no items.

        int rows = MathStuff.fastCeil(list.size() / 9d);
        stacks.addAll(list);
        int left = (rows * 9) - list.size();
        for (int i = 0; i < left; i++) {
            stacks.add(ItemStack.EMPTY); //fill the gaps
        }
        if (lineBreak) stacks.addAll(DefaultedList.ofSize(9, ItemStack.EMPTY)); //line break
    }
}
