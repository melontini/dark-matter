package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.api.base.util.mixin.annotations.AsmTransformers;
import me.melontini.dark_matter.api.base.util.mixin.annotations.Publicize;
import me.melontini.dark_matter.api.enums.EnumUtils;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import me.melontini.dark_matter.impl.enums.interfaces.EnchantmentTargetHack;
import me.melontini.dark_matter.impl.enums.transformers.EnchantmentTargetTransformer;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Pseudo
@AsmTransformers(EnchantmentTargetTransformer.class)
@Mixin(value = EnchantmentTarget.class, priority = 1001)
public class EnchantmentTargetMixin implements ExtendableEnum, EnchantmentTargetHack {
    @Shadow
    @Final
    @Mutable
    private static EnchantmentTarget[] field_9077;

    @Invoker("<init>")
    static EnchantmentTarget dark_matter$invokeCtx(String internalName, int id) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    @SuppressWarnings("MixinAnnotationTarget")
    @Inject(at = @At("RETURN"), method = "isAcceptableItem", cancellable = true)
    private void dark_matter$checkPredicate(Item par1, CallbackInfoReturnable<Boolean> cir) {
        if (dark_matter$predicate != null) cir.setReturnValue(dark_matter$predicate.test(par1));
    }

    @Unique
    private Predicate<Item> dark_matter$predicate;

    @Unique
    @Publicize
    private static EnchantmentTarget dark_matter$extendEnum(String internalName, Predicate<Item> predicate) {
        for (EnchantmentTarget target : field_9077) {
            if (target.name().equalsIgnoreCase(internalName)) return target;
        }

        EnchantmentTarget last = field_9077[field_9077.length - 1];
        EnchantmentTarget enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1);
        ((EnchantmentTargetHack) (Object) enumConst).dark_matter$setPredicate(predicate);
        field_9077 = ArrayUtils.add(field_9077, enumConst);
        EnumUtils.clearEnumCache(EnchantmentTarget.class);
        DarkMatterLog.debug("Extended enum {}", enumConst);
        return enumConst;
    }

    @Override
    public void dark_matter$setPredicate(Predicate<Item> predicate) {
        this.dark_matter$predicate = predicate;
    }


    @Override
    public <T extends Enum<?>> T dark_matter$extend(String internalName, Object... params) {
        return (T) dark_matter$extendEnum(internalName, (Predicate<Item>) params[0]);
    }
}
