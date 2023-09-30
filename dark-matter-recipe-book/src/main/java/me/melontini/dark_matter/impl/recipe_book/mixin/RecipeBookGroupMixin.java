package me.melontini.dark_matter.impl.recipe_book.mixin;

import me.melontini.dark_matter.impl.recipe_book.RecipeBookInternals;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookGroup.class)
public class RecipeBookGroupMixin {

    @Inject(at = @At("HEAD"), method = "getGroups", cancellable = true)
    private static void dark_matter$getGroups(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        RecipeBookInternals.getGroups(category).ifPresent(cir::setReturnValue);
    }
}
