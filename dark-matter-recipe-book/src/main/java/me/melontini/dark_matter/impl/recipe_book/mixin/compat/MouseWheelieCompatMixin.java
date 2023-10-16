package me.melontini.dark_matter.impl.recipe_book.mixin.compat;

import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = RecipeBookWidget.class, priority = 1500)
abstract class MouseWheelieCompatMixin {

    @Shadow
    @Nullable
    private RecipeGroupButtonWidget currentTab;

    @Dynamic("MixinRecipeBookWidget.class from mod MouseWheelie")
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/recipebook/RecipeBookWidget.refreshResults(Z)V", shift = At.Shift.BEFORE), method = "mouseWheelie_scrollRecipeBook", require = 0)
    private void dark_matter$scrollPages(double mouseX, double mouseY, double scrollAmount, CallbackInfoReturnable<?> cir) {
        if (this.currentTab == null) return;//how tho?
        RecipeBookWidget bookWidget = (RecipeBookWidget) (Object) this;
        if (bookWidget.dm$getPage() != currentTab.dm$getPage()) {
            bookWidget.dm$setPage(currentTab.dm$getPage());
        }
    }
}
