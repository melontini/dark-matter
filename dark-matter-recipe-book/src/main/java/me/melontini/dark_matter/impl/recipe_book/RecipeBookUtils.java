package me.melontini.dark_matter.impl.recipe_book;

import com.mojang.datafixers.util.Pair;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.MakeSure;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookOptions;

@UtilityClass
public class RecipeBookUtils {

    public static RecipeBookCategory createCategory(String internalName) {
        MakeSure.notEmpty(internalName, "Tried to create a RecipeBookCategory with an empty string.");

        RecipeBookCategory category = RecipeBookCategory.values()[0].dark_matter$extend(internalName);

        RecipeBookOptions.CATEGORY_OPTION_NAMES.putIfAbsent(category, new Pair<>("is" + internalName + "GuiOpen", "is" + internalName + "FilteringCraftable"));
        return category;
    }
}
