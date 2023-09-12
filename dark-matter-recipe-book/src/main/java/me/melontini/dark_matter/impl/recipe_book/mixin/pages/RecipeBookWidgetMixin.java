package me.melontini.dark_matter.impl.recipe_book.mixin.pages;

import me.melontini.dark_matter.api.base.util.MathStuff;
import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import me.melontini.dark_matter.api.recipe_book.interfaces.PaginatedRecipeBookWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin implements PaginatedRecipeBookWidget {

    @Unique
    private static final ButtonTextures DM$PAGE_FORWARD_TEXTURES = new ButtonTextures(
            new Identifier("recipe_book/page_forward"), new Identifier("recipe_book/page_forward_highlighted")
    );
    @Unique
    private static final ButtonTextures DM$PAGE_BACKWARD_TEXTURES = new ButtonTextures(
            new Identifier("recipe_book/page_backward"), new Identifier("recipe_book/page_backward_highlighted")
    );

    @Shadow
    protected MinecraftClient client;
    @Shadow
    private int parentWidth;
    @Shadow
    private int parentHeight;
    @Shadow
    private int leftOffset;
    @Shadow
    @Final
    private List<RecipeGroupButtonWidget> tabButtons;
    @Shadow
    private ClientRecipeBook recipeBook;
    @Unique
    private int page = 0;
    @Unique
    private int pages;
    @Unique
    private ToggleButtonWidget nextPageButton;
    @Unique
    private ToggleButtonWidget prevPageButton;

    @Shadow
    public abstract boolean isOpen();

    @Shadow @Final public static int field_32408;

    @Shadow @Final public static int field_32409;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;setToggled(Z)V", shift = At.Shift.BEFORE), method = "reset")
    private void dark_matter$reset(CallbackInfo ci) {
        int a = (this.parentWidth - dm$horizontalOffset()) / 2 - this.leftOffset;
        int s = (this.parentHeight + dm$verticalOffset()) / 2;
        this.nextPageButton = new ToggleButtonWidget(a + 14, s, 12, 17, false);
        this.nextPageButton.setTextures(DM$PAGE_FORWARD_TEXTURES);
        this.prevPageButton = new ToggleButtonWidget(a - 35, s, 12, 17, true);
        this.prevPageButton.setTextures(DM$PAGE_BACKWARD_TEXTURES);
        this.page = 0;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE), method = "render")
    private void dark_matter$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        dark_matter$renderPageText(context);
        this.prevPageButton.render(context, mouseX, mouseY, delta);
        this.nextPageButton.render(context, mouseX, mouseY, delta);
    }

    @Unique
    private void dark_matter$renderPageText(DrawContext context) {
        int x = (this.parentWidth - (dm$horizontalOffset() + 12)) / 2 - this.leftOffset - 30;
        int y = (this.parentHeight + (dm$verticalOffset() + 3)) / 2 + 3;
        if (this.pages > 1) {
            String string = this.page + 1 + "/" + this.pages;
            int textLength = this.client.textRenderer.getWidth(string);
            context.drawText(this.client.textRenderer, string, (int) (x - textLength / 2F + 20F), y, -1, false);
        }
    }

    @Unique
    @Override
    public void dm$updatePages() {
        for (RecipeGroupButtonWidget widget : this.tabButtons) {
            widget.visible = widget.dm$getPage() == this.page;
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    private void dark_matter$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.player != null) if (this.isOpen() && !this.client.player.isSpectator()) {
            if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (this.page < (this.pages - 1)) dm$setPage(++this.page);
                cir.setReturnValue(true);
            } else if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (this.page > 0) dm$setPage(--this.page);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    @Override
    public void dm$updatePageSwitchButtons() {
        if (this.nextPageButton != null) this.nextPageButton.visible = this.pages > 1 && this.page < (this.pages - 1);
        if (this.prevPageButton != null) this.prevPageButton.visible = this.pages > 1 && this.page != 0;
    }

    @Inject(at = @At("HEAD"), method = "refreshTabButtons", cancellable = true)
    private void dark_matter$refresh(CallbackInfo ci) {
        this.pages = 0;
        int wc = 0;
        int x = (this.parentWidth - dm$horizontalOffset()) / 2 - this.leftOffset - 30;
        int y = (this.parentHeight - dm$verticalOffset()) / 2 + 3;
        int index = 0;

        for (RecipeGroupButtonWidget widget : this.tabButtons) {
            if (RecipeBookHelper.isSearchGroup(widget.getCategory())) {
                widget.visible = true;

                widget.dm$setPage((int) Math.floor(wc / 6f));
                widget.setPosition(x, y + (widget.getHeight() * index++));
                if (index == 6) index = 0;
                wc++;
            } else if (widget.hasKnownRecipes(this.recipeBook)) {
                widget.checkForNewRecipes(this.client);

                widget.dm$setPage((int) Math.floor(wc / 6f));
                widget.setPosition(x, y + (widget.getHeight() * index++));
                if (index == 6) index = 0;
                wc++;
            }
        }

        this.pages = MathStuff.fastCeil(wc / 6f);
        this.dm$updatePages();
        this.dm$updatePageSwitchButtons();
        ci.cancel();
    }

    @Unique
    @Override
    public int dm$getPage() {
        return this.page;
    }

    @Unique
    @Override
    public void dm$setPage(int page) {
        if (page < 0) page = 0;
        if (page > pages - 1) page = pages - 1;

        this.page = page;
        dm$updatePages();
        dm$updatePageSwitchButtons();
    }

    @Unique
    @Override
    public int dm$getPageCount() {
        return this.pages;
    }

    @Unique
    private static int dm$horizontalOffset() {
        return field_32408;
    }

    @Unique
    private static int dm$verticalOffset() {
        return field_32409;
    }
}
