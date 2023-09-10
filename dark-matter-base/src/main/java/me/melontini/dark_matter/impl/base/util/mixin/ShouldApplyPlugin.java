package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.base.util.mixin.MixinShouldApply;
import me.melontini.dark_matter.api.base.util.mixin.Mod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Annotations;

import java.util.List;
import java.util.Map;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

@ApiStatus.Internal
public final class ShouldApplyPlugin implements IPluginPlugin {

    private static final String SHOULD_APPLY_DESC = "L" + MixinShouldApply.class.getName().replace(".", "/") + ";";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName, ClassNode mixinNode, List<AnnotationNode> mergedAnnotations) {
        AnnotationNode node = Annotations.getVisible(mixinNode, MixinShouldApply.class);
        if (node == null) return true;

        Map<String, Object> values = AsmUtil.mapAnnotationNode(node);
        if (values.isEmpty()) return true;

        return checkMods(node) && checkMCVersion(node);
    }

    @Override
    public void afterApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (targetClass.visibleAnnotations != null && !targetClass.visibleAnnotations.isEmpty()) {
            targetClass.visibleAnnotations.removeIf(node -> SHOULD_APPLY_DESC.equals(node.desc));
        }
    }

    private static boolean checkMods(AnnotationNode node) {
        List<Map<String, Object>> array = AsmUtil.getAnnotationValue(node, "mods", AsmUtil.emptyAnnotationList());
        if (array.isEmpty()) return true;

        for (Map<String, Object> map : array) {
            String name = cast(map.get("value"));
            Mod.Mode mode = cast(map.getOrDefault("mode", Mod.Mode.LOADED));

            return switch (mode) {
                case LOADED -> FabricLoader.getInstance().isModLoaded(name);
                case NOT_LOADED -> !FabricLoader.getInstance().isModLoaded(name);
            };
        }

        return true;
    }

    private static boolean checkMCVersion(AnnotationNode node) {
        String version = AsmUtil.getAnnotationValue(node, "mcVersion", "");

        if (version.isEmpty()) return true;

        try {
            var predicate = VersionPredicate.parse(version);
            return predicate.test(FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getMetadata().getVersion());
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

}
