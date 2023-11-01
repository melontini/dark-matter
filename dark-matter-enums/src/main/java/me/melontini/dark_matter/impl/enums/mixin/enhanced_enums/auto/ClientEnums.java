package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums.auto;

import me.melontini.dark_matter.api.base.util.mixin.annotations.AsmTransformers;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.impl.enums.transformers.StaticEnumTransformer;
import net.minecraft.client.recipebook.RecipeBookGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@AsmTransformers(StaticEnumTransformer.class)
@Mixin(value = RecipeBookGroup.class, priority = 1100)
public class ClientEnums implements ExtendableEnum {
}
