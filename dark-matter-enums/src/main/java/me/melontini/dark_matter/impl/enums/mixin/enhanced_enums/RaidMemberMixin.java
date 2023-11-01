package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.api.base.util.mixin.annotations.AsmTransformers;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.impl.enums.transformers.StaticEnumTransformer;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.*;

@Pseudo
@AsmTransformers(StaticEnumTransformer.class)
@Mixin(value = Raid.Member.class, priority = 1001)
public abstract class RaidMemberMixin implements ExtendableEnum {
    @Shadow
    @Final
    @Mutable
    private static Raid.Member[] VALUES;

    @Override
    public void dark_matter$init(Object[] args) {
        VALUES = Raid.Member.values();
    }
}
