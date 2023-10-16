package me.melontini.dark_matter.impl.recipe_book.mixin.make_mutable;

import com.mojang.datafixers.util.Pair;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = RecipeBookOptions.class, priority = 800)
public class RecipeBookOptionsMixin {

    @Shadow
    @Final
    @Mutable
    private static Map<RecipeBookCategory, Pair<String, String>> CATEGORY_OPTION_NAMES;

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void dark_matter$makeMutable(CallbackInfo ci) {
        CATEGORY_OPTION_NAMES = new HashMap<>(CATEGORY_OPTION_NAMES);
    }
}
