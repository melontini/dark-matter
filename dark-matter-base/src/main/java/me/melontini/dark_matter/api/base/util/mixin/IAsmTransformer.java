package me.melontini.dark_matter.api.base.util.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public interface IAsmTransformer {

    default void beforeApply(ClassNode targetClass, IMixinInfo mixinInfo) {

    }

    default void afterApply(ClassNode targetClass, IMixinInfo mixinInfo) {

    }
}
