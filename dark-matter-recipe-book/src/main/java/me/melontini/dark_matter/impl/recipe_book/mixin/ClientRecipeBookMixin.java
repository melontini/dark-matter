package me.melontini.dark_matter.impl.recipe_book.mixin;

import me.melontini.dark_matter.impl.recipe_book.ClientRecipeBookUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.BiFunction;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void dark_matter$getGroupForRecipe(RecipeEntry<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        var rm = Optional.ofNullable(MinecraftClient.getInstance().getNetworkHandler())
                .map(ClientPlayNetworkHandler::getRegistryManager).orElse(null);
        ClientRecipeBookUtils.getLookups(recipe.value().getType()).map(functions -> {
            RecipeBookGroup result;
            for (BiFunction<RecipeEntry<?>, DynamicRegistryManager, RecipeBookGroup> function : functions) {
                if ((result = function.apply(recipe, rm)) != null) {
                    return result;
                }
            }
            return null;
        }).ifPresent(cir::setReturnValue);
    }
}
