package me.melontini.dark_matter.impl.recipe_book.mixin.pages;

import me.melontini.dark_matter.api.recipe_book.interfaces.PaginatedRecipeGroupButtonWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RecipeGroupButtonWidget.class)
public abstract class RecipeGroupButtonMixin implements PaginatedRecipeGroupButtonWidget {

    @Unique
    private int dm$page = -1;

    @Override
    public int dm$getPage() {
        return dm$page;
    }

    @Override
    public void dm$setPage(int page) {
        this.dm$page = page;
    }
}
