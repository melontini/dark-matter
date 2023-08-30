package me.melontini.dark_matter.impl.recipe_book.mixin;

import me.melontini.dark_matter.impl.recipe_book.RecipeBookInternals;
import net.minecraft.class_8786;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiFunction;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Unique
    private static DynamicRegistryManager dark_matter$currentDynamicRegistryManager;

    @Inject(at = @At("HEAD"), method = "reload")
    private void dark_matter$reloadHead(Iterable<Recipe<?>> recipes, DynamicRegistryManager registryManager, CallbackInfo ci) {
        dark_matter$currentDynamicRegistryManager = registryManager;
    }

    @Inject(at = @At("TAIL"), method = "reload")
    private void dark_matter$reloadTail(Iterable<Recipe<?>> recipes, DynamicRegistryManager registryManager, CallbackInfo ci) {
        dark_matter$currentDynamicRegistryManager = null;
    }

    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void dark_matter$getGroupForRecipe(class_8786<?> arg, CallbackInfoReturnable<RecipeBookGroup> cir) {
        RecipeBookInternals.getLookups(arg.comp_1933().getType()).map(functions -> {
            RecipeBookGroup result;
            for (BiFunction<Recipe<?>, DynamicRegistryManager, RecipeBookGroup> function : functions) {
                if ((result = function.apply(arg.comp_1933(), dark_matter$currentDynamicRegistryManager)) != null) {
                    return result;
                }
            }
            return null;
        }).ifPresent(cir::setReturnValue);
    }
}
