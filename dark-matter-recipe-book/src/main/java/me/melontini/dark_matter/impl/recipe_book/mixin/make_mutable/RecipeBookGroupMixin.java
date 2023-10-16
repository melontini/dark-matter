package me.melontini.dark_matter.impl.recipe_book.mixin.make_mutable;

import net.minecraft.client.recipebook.RecipeBookGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = RecipeBookGroup.class, priority = 800)
public class RecipeBookGroupMixin {

    @Shadow
    @Final
    @Mutable
    public static List<RecipeBookGroup> SMOKER;
    @Shadow
    @Final
    @Mutable
    public static List<RecipeBookGroup> BLAST_FURNACE;
    @Shadow
    @Final
    @Mutable
    public static List<RecipeBookGroup> FURNACE;
    @Shadow
    @Final
    @Mutable
    public static List<RecipeBookGroup> CRAFTING;
    @Shadow
    @Final
    @Mutable
    public static Map<RecipeBookGroup, List<RecipeBookGroup>> SEARCH_MAP;

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void dark_matter$makeMutable(CallbackInfo ci) {
        SMOKER = new ArrayList<>(SMOKER);
        BLAST_FURNACE = new ArrayList<>(BLAST_FURNACE);
        FURNACE = new ArrayList<>(FURNACE);
        CRAFTING = new ArrayList<>(CRAFTING);

        Map<RecipeBookGroup, List<RecipeBookGroup>> newMap = new HashMap<>();
        SEARCH_MAP.forEach((group, groups) -> newMap.put(group, new ArrayList<>(groups)));
        SEARCH_MAP = newMap;
    }
}
