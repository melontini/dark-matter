package me.melontini.dark_matter.impl.recipe_book;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import me.melontini.dark_matter.api.enums.EnumWrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.DynamicRegistryManager;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@UtilityClass
public class ClientRecipeBookUtils {

    private static final Map<RecipeType<?>, List<BiFunction<RecipeEntry<?>, DynamicRegistryManager, RecipeBookGroup>>> GROUP_LOOKUPS = new HashMap<>();

    private static final Map<RecipeBookCategory, List<RecipeBookGroup>> GROUPS_FOR_CATEGORY = new HashMap<>();

    private static final Lazy<Map<RecipeBookCategory, Supplier<List<RecipeBookGroup>>>> VANILLA_CATEGORIES = Lazy.of(() -> () -> Utilities.consume(new HashMap<>(), map -> {
        map.put(RecipeBookCategory.CRAFTING, () -> RecipeBookGroup.CRAFTING);
        map.put(RecipeBookCategory.FURNACE, () -> RecipeBookGroup.FURNACE);
        map.put(RecipeBookCategory.BLAST_FURNACE, () -> RecipeBookGroup.BLAST_FURNACE);
        map.put(RecipeBookCategory.SMOKER, () -> RecipeBookGroup.SMOKER);
    }));

    private static boolean isVanillaCategory(RecipeBookCategory category) {
        return VANILLA_CATEGORIES.get().containsKey(category);
    }

    private static List<RecipeBookGroup> getGroupsForCategory(RecipeBookCategory category) {
        return VANILLA_CATEGORIES.get().get(category).get();
    }

    public static void registerGroupLookup(RecipeType<?> type, BiFunction<RecipeEntry<?>, DynamicRegistryManager, RecipeBookGroup> function) {
        MakeSure.notNulls(type, function);
        GROUP_LOOKUPS.computeIfAbsent(type, type1 -> new ArrayList<>(1)).add(0, function);
    }

    public static void registerGroups(RecipeBookCategory category, List<RecipeBookGroup> groups) {
        MakeSure.notNulls(category, groups);
        if (isVanillaCategory(category)) {
            List<RecipeBookGroup> groupList = getGroupsForCategory(category);
            (groups = new ArrayList<>(groups)).removeIf(groupList::contains); //Convert to ArrayList to keep mutability
            groupList.addAll(groups);
            return;
        }

        List<RecipeBookGroup> groupList = GROUPS_FOR_CATEGORY.computeIfAbsent(category, category1 -> new ArrayList<>());
        (groups = new ArrayList<>(groups)).removeIf(groupList::contains); //Convert to ArrayList to keep mutability
        groupList.addAll(groups);
    }

    public static void registerGroups(RecipeBookCategory category, int index, List<RecipeBookGroup> groups) {
        MakeSure.notNulls(category, groups);
        MakeSure.isTrue(index >= 0, "Index can't be below 0!");
        if (isVanillaCategory(category)) {
            List<RecipeBookGroup> groupList = getGroupsForCategory(category);
            (groups = new ArrayList<>(groups)).removeIf(groupList::contains); //Convert to ArrayList to keep mutability

            if (index >= groupList.size()) groupList.addAll(groups);
            else groupList.addAll(index, groups);
            return;
        }

        List<RecipeBookGroup> groupList = GROUPS_FOR_CATEGORY.computeIfAbsent(category, category1 -> new ArrayList<>());
        (groups = new ArrayList<>(groups)).removeIf(groupList::contains); //Convert to ArrayList to keep mutability

        if (index >= groupList.size()) groupList.addAll(groups);
        else groupList.addAll(index, groups);
    }

    public static void addToSearchGroup(RecipeBookGroup searchGroup, List<RecipeBookGroup> groups) {
        MakeSure.notNulls(searchGroup, groups);
        List<RecipeBookGroup> groupList = RecipeBookGroup.SEARCH_MAP.computeIfAbsent(MakeSure.notNull(searchGroup), group -> new ArrayList<>());
        (groups = new ArrayList<>(groups)).removeIf(groupList::contains); //Convert to ArrayList to keep mutability
        groupList.addAll(groups);
    }

    public static void addToSearchGroup(RecipeBookGroup searchGroup, int index, List<RecipeBookGroup> groups) {
        MakeSure.notNulls(searchGroup, groups);
        List<RecipeBookGroup> groupList = RecipeBookGroup.SEARCH_MAP.computeIfAbsent(MakeSure.notNull(searchGroup), group -> new ArrayList<>());
        (groups = new ArrayList<>(groups)).removeIf(groupList::contains); //Convert to ArrayList to keep mutability

        if (index >= groupList.size()) groupList.addAll(groups);
        else groupList.addAll(index, groups);
    }

    public static RecipeBookGroup createGroup(String internalName, ItemStack... stacks) {
        MakeSure.notEmpty(internalName, "Tried to create a RecipeBookGroup with an empty string.");

        return EnumWrapper.RecipeBookGroup.extend(internalName, stacks);
    }

    public static Optional<List<BiFunction<RecipeEntry<?>, DynamicRegistryManager, RecipeBookGroup>>> getLookups(RecipeType<?> type) {
        return GROUP_LOOKUPS.containsKey(type) ? Optional.of(Collections.unmodifiableList(GROUP_LOOKUPS.get(type))) : Optional.empty();
    }

    public static Optional<List<RecipeBookGroup>> getGroups(RecipeBookCategory category) {
        return GROUPS_FOR_CATEGORY.containsKey(category) ? Optional.of(Collections.unmodifiableList(GROUPS_FOR_CATEGORY.get(category))) : Optional.empty();
    }
}
