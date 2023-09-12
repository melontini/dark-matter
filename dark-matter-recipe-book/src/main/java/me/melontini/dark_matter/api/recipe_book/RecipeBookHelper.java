package me.melontini.dark_matter.api.recipe_book;

import me.melontini.dark_matter.impl.recipe_book.RecipeBookInternals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class RecipeBookHelper {

    private RecipeBookHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Allows you to map a recipe to a group.
     * <p>
     * This supports adding multiple lookups for the same type. So, multiple mods can map their recipes of the same type.
     * <p>
     * It's recommended to keep this simple, as it will be run for every recipe.
     * @param lookup The lookup function. Please note, the function must return null if the recipe doesn't match.
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroupLookup(@NotNull RecipeType<?> type, @NotNull Function<Recipe<?>, @Nullable RecipeBookGroup> lookup) {
        RecipeBookInternals.registerGroupLookup(type, (recipe, dynamicRegistryManager) -> lookup.apply(recipe));
    }

    /**
     * Allows you to map a recipe to a group, but with a dynamic registry manager.
     * <p>
     * Please, use the above method if you don't need this, since the registry might be null if {@link RecipeBookGroup#getGroups(RecipeBookCategory)} wasn't called by {@link net.minecraft.client.recipebook.ClientRecipeBook#reload(Iterable, DynamicRegistryManager)}
     * <p>
     * This supports adding multiple lookups for the same type. So, multiple mods can map their recipes of the same type.
     * <p>
     * It's recommended to keep this simple, as it will be run for every recipe.
     * @param lookup The lookup function. Please note, the function must return null if the recipe doesn't match.
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroupLookup(@NotNull RecipeType<?> type, @NotNull BiFunction<Recipe<?>, @Nullable DynamicRegistryManager, @Nullable RecipeBookGroup> lookup) {
        RecipeBookInternals.registerGroupLookup(type, lookup);
    }

    //
    // Register groups.
    //

    /**
     * Register groups for a category.
     * <p>
     * You'll also have to call {@link RecipeBookHelper#addToSearchGroup(RecipeBookGroup, RecipeBookGroup...)} to add the groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroups(@NotNull RecipeBookCategory category, RecipeBookGroup... groups) {
        registerGroups(category, Arrays.asList(groups));
    }

    /**
     * Register groups for a category. Here you can specify the index for the groups.
     * <p>
     * You'll also have to call {@link RecipeBookHelper#addToSearchGroup(RecipeBookGroup, RecipeBookGroup...)} to add the groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroups(@NotNull RecipeBookCategory category, int index, RecipeBookGroup... groups) {
        registerGroups(category, index, Arrays.asList(groups));
    }

    /**
     * Register groups for a category.
     * <p>
     * You'll also have to call {@link RecipeBookHelper#addToSearchGroup(RecipeBookGroup, RecipeBookGroup...)} to add the groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroups(@NotNull RecipeBookCategory category, List<RecipeBookGroup> groups) {
        RecipeBookInternals.registerGroups(category, groups);
    }

    /**
     * Register groups for a category. Here you can specify the index for the groups.
     * <p>
     * You'll also have to call {@link RecipeBookHelper#addToSearchGroup(RecipeBookGroup, RecipeBookGroup...)} to add the groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroups(@NotNull RecipeBookCategory category, int index, @NotNull List<RecipeBookGroup> groups) {
        RecipeBookInternals.registerGroups(category, index, groups);
    }

    //
    // Add to search.
    //

    /**
     * Add groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(@NotNull RecipeBookGroup searchGroup, RecipeBookGroup... groups) {
        addToSearchGroup(searchGroup, Arrays.asList(groups));
    }

    /**
     * Add groups to the search group. Here you can specify the index for the groups.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(@NotNull RecipeBookGroup searchGroup, int index, RecipeBookGroup... groups) {
        addToSearchGroup(searchGroup, index, Arrays.asList(groups));
    }

    /**
     * Add groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(@NotNull RecipeBookGroup searchGroup, @NotNull List<RecipeBookGroup> groups) {
        RecipeBookInternals.addToSearchGroup(searchGroup, groups);
    }

    /**
     * Add groups to the search group. Here you can specify the index for the groups.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(@NotNull RecipeBookGroup searchGroup, int index, @NotNull List<RecipeBookGroup> groups) {
        RecipeBookInternals.addToSearchGroup(searchGroup, index, groups);
    }

    //
    // Register and add to search.
    //

    /**
     * Register and add to search.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(@NotNull RecipeBookCategory category, @NotNull RecipeBookGroup searchGroup, RecipeBookGroup... groups) {
        registerAndAddToSearch(category, searchGroup, Arrays.asList(groups));
    }

    /**
     * Register and add to search. Here you can specify the index for the groups.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(@NotNull RecipeBookCategory category, @NotNull RecipeBookGroup searchGroup, int index, RecipeBookGroup... groups) {
        registerAndAddToSearch(category, searchGroup, index, Arrays.asList(groups));
    }

    /**
     * Register and add to search.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(@NotNull RecipeBookCategory category, @NotNull RecipeBookGroup searchGroup, @NotNull List<RecipeBookGroup> groups) {
        RecipeBookInternals.registerGroups(category, groups);
        groups.remove(searchGroup);
        RecipeBookInternals.addToSearchGroup(searchGroup, groups);
    }

    /**
     * Register and add to search. Here you can specify the index for the groups.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(@NotNull RecipeBookCategory category, @NotNull RecipeBookGroup searchGroup, int index, @NotNull List<RecipeBookGroup> groups) {
        RecipeBookInternals.registerGroups(category, index, groups);
        groups.remove(searchGroup);
        RecipeBookInternals.addToSearchGroup(searchGroup, Math.max(index - 1, 0), groups);
    }

    //
    // Creating groups and categories
    //

    /**
     * Creates a {@link RecipeBookCategory}. This is meant to be used in the main init of your mod.
     */
    public static RecipeBookCategory createCategory(@NotNull Identifier id) {
        return RecipeBookInternals.createCategory(id.toString().replace('/', '_').replace(':', '_'));
    }

    /**
     * Creates a {@link RecipeBookGroup}. This is meant to be used in the client init of your mod.
     * @param stacks This is the icon displayed in the recipe book. Values above 2 do not work.
     */
    @Environment(EnvType.CLIENT)
    public static RecipeBookGroup createGroup(@NotNull Identifier id, ItemStack... stacks) {
        return RecipeBookInternals.createGroup(id.toString().replace('/', '_').replace(':', '_'), stacks);
    }

    @Environment(EnvType.CLIENT)
    public static boolean isSearchGroup(@NotNull RecipeBookGroup group) {
        return RecipeBookGroup.SEARCH_MAP.containsKey(group);
    }

    //
    // Old, deprecated code.
    //

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addRecipePredicate(RecipeType<?> type, Function<Recipe<?>, RecipeBookGroup> function) {
        registerGroupLookup(type, function);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, RecipeBookGroup group) {
        registerGroups(category, group);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, int index, RecipeBookGroup group) {
        registerGroups(category, index, group);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, List<RecipeBookGroup> groups) {
        registerGroups(category, groups);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, int index, List<RecipeBookGroup> groups) {
        registerGroups(category, index, groups);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToSearchMap(RecipeBookGroup searchGroup, List<RecipeBookGroup> groups) {
        addToSearchGroup(searchGroup, groups);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToSearchMap(RecipeBookGroup searchGroup, int index, List<RecipeBookGroup> groups) {
        addToSearchGroup(searchGroup, index, groups);
    }

}
