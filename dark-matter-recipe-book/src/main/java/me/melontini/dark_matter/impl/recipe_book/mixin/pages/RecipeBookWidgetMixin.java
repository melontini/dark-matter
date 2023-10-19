package me.melontini.dark_matter.impl.recipe_book.mixin.pages;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.melontini.dark_matter.api.base.util.MathStuff;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import me.melontini.dark_matter.api.recipe_book.interfaces.PaginatedRecipeBookWidget;
import me.melontini.dark_matter.impl.recipe_book.RecipeBookPageButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = RecipeBookWidget.class, priority = 999)
public abstract class RecipeBookWidgetMixin implements PaginatedRecipeBookWidget {

    @Shadow protected MinecraftClient client;
    @Shadow private int parentWidth;
    @Shadow private int parentHeight;
    @Shadow private int leftOffset;
    @Shadow @Final private List<RecipeGroupButtonWidget> tabButtons;
    @Shadow public abstract boolean isOpen();
    @Shadow @Final public static int field_32408;
    @Shadow @Final public static int field_32409;

    @Shadow @Nullable private RecipeGroupButtonWidget currentTab;
    @Unique
    private int dm$page = 0;
    @Unique
    private int dm$pages;
    @Unique
    private RecipeBookPageButton dm$nextPageButton;
    @Unique
    private RecipeBookPageButton dm$prevPageButton;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;setToggled(Z)V", shift = At.Shift.BEFORE), method = "reset")
    private void dark_matter$reset(CallbackInfo ci) {
        int a = (this.parentWidth - dm$horizontalOffset()) / 2 - this.leftOffset;
        int s = (this.parentHeight - dm$verticalOffset()) / 2;
        this.dm$nextPageButton = new RecipeBookPageButton(a + 18, s - 13, Utilities.cast(this), true);
        this.dm$prevPageButton = new RecipeBookPageButton(a + 3, s - 13, Utilities.cast(this), false);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE), method = "render")
    private void dark_matter$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.dm$prevPageButton.render(context, mouseX, mouseY, delta);
        this.dm$nextPageButton.render(context, mouseX, mouseY, delta);
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    private void dark_matter$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.player != null) if (this.isOpen() && !this.client.player.isSpectator()) {
            if (this.dm$nextPageButton.mouseClicked(mouseX, mouseY, button)) {
                this.dm$incrementPage();
                cir.setReturnValue(true);
            } else if (this.dm$prevPageButton.mouseClicked(mouseX, mouseY, button)) {
                this.dm$decrementPage();
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "refreshResults")
    private void dark_matter$refreshResults(boolean resetCurrentPage, CallbackInfo ci) {
        if (resetCurrentPage && this.currentTab != null) {
            if (this.dm$getPage() != this.currentTab.dm$getPage()) {
                this.dm$setPage(Math.max(this.currentTab.dm$getPage(), 0));
            }
        }
    }

    @ModifyExpressionValue(at = @At(value = "FIELD", target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;CRAFTING_SEARCH:Lnet/minecraft/client/recipebook/RecipeBookGroup;"), method = "refreshTabButtons", require = 0)
    private RecipeBookGroup dark_matter$refresh$correctGroup(RecipeBookGroup group, @Local RecipeGroupButtonWidget widget) {
        return RecipeBookHelper.isSearchGroup(widget.getCategory()) ? widget.getCategory() : group;
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;setPosition(II)V"), method = "refreshTabButtons", index = 1)
    private int dark_matter$refresh$setPos(int y, @Local RecipeGroupButtonWidget widget, @Local(ordinal = 1) int j, @Share("index") LocalIntRef index) {
        int pos = j + widget.getHeight() * index.get();
        index.set(index.get() + 1);
        return pos;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;setPosition(II)V", shift = At.Shift.AFTER), method = "refreshTabButtons")
    private void dark_matter$refresh$setPos(CallbackInfo ci, @Local RecipeGroupButtonWidget widget, @Share("index") LocalIntRef index, @Share("wc") LocalIntRef wc) {
        widget.dm$setPage((int) Math.floor(wc.get() / 6f));
        if (index.get() == 6) index.set(0);
        wc.set(wc.get() + 1);
    }

    @Inject(at = @At("TAIL"), method = "refreshTabButtons")
    private void dark_matter$refresh$tail(CallbackInfo ci, @Share("wc") LocalIntRef wc) {
        this.dm$pages = MathStuff.fastCeil(wc.get() / 6f);
        this.dm$updatePages();
        this.dm$updatePageSwitchButtons();
    }

    @Unique
    private static int dm$horizontalOffset() {
        return field_32408;
    }

    @Unique
    private static int dm$verticalOffset() {
        return field_32409;
    }

    @Unique
    @Override
    public void dm$updatePages() {
        for (RecipeGroupButtonWidget widget : this.tabButtons) {
            widget.visible = widget.dm$getPage() == this.dm$page;
        }
    }

    @Unique
    @Override
    public void dm$updatePageSwitchButtons() {
        if (this.dm$nextPageButton != null) {
            this.dm$nextPageButton.visible = this.dm$getPageCount() > 1;
            this.dm$nextPageButton.active = this.dm$getPage() < (this.dm$getPageCount() - 1);
        }
        if (this.dm$prevPageButton != null) {
            this.dm$prevPageButton.visible = this.dm$getPageCount() > 1;
            this.dm$prevPageButton.active = this.dm$getPage() > 0;
        }
    }

    @Unique
    @Override
    public int dm$getPage() {
        return this.dm$page;
    }

    @Unique
    @Override
    public void dm$setPage(int page) {
        if (page < 0) page = 0;
        if (page > dm$pages - 1) page = dm$pages - 1;

        this.dm$page = page;
        dm$updatePages();
        dm$updatePageSwitchButtons();
    }

    @Unique
    @Override
    public int dm$getPageCount() {
        return this.dm$pages;
    }
}
