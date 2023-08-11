package me.melontini.dark_matter.api.content.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;

public interface ItemGroupExtensions {

    @Environment(EnvType.CLIENT)
    default boolean dm$shouldAnimateIcon() {
        return false;
    }

    default ItemGroup dm$setIconAnimation(AnimatedItemGroup animation) {
        throw new IllegalStateException("Interface not implemented");
    }

    @Environment(EnvType.CLIENT)
    default AnimatedItemGroup dm$getIconAnimation() {
        throw new IllegalStateException("Interface not implemented");
    }

    @Environment(EnvType.CLIENT)
    @Deprecated
    default boolean shouldAnimateIcon() {
        return dm$shouldAnimateIcon();
    }

    @Deprecated
    default ItemGroup setIconAnimation(AnimatedItemGroup animation) {
        return dm$setIconAnimation(animation);
    }

    @Deprecated
    @Environment(EnvType.CLIENT)
    default AnimatedItemGroup getIconAnimation() {
        return dm$getIconAnimation();
    }
}