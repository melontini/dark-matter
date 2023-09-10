package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.base.util.mixin.annotations.MixinPredicate;
import me.melontini.dark_matter.api.base.util.mixin.annotations.Mod;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Annotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

@ApiStatus.Internal
public class MixinPredicatePlugin implements IPluginPlugin {

    private static final String PREDICATE_DESC = "L" + MixinPredicate.class.getName().replace(".", "/") + ";";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName, ClassNode mixinNode, List<AnnotationNode> mergedAnnotations) {
        AnnotationNode node = Annotations.getVisible(mixinNode, MixinPredicate.class);
        if (node == null) return true;

        Map<String, Object> values = AsmUtil.mapAnnotationNode(node);
        if (values.isEmpty()) return true;

        return checkMods(values);
    }

    @Override
    public void afterApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (targetClass.visibleAnnotations != null && !targetClass.visibleAnnotations.isEmpty()) {
            targetClass.visibleAnnotations.removeIf(node -> PREDICATE_DESC.equals(node.desc));
        }
    }

    private static boolean checkMods(@NotNull Map<String, Object> values) {
        List<Map<String, Object>> array = cast(values.getOrDefault("mods", AsmUtil.emptyAnnotationList()));
        if (array.isEmpty()) return true;

        for (Map<String, Object> map : array) {
            try {
                String id = cast(map.get("value"));
                Mod.State state = cast(map.getOrDefault("state", Mod.State.LOADED));
                String version = cast(map.getOrDefault("version", "*"));
                VersionPredicate predicate = VersionPredicate.parse(version);

                Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(id);

                return switch (state) {
                    case LOADED -> {
                        boolean b = container.filter(modContainer -> predicate.test(modContainer.getMetadata().getVersion())).isPresent();
                        DarkMatterLog.debug("Checking mod {}. {} ({}): {}", id, state, version, b);
                        yield b;
                    }
                    case NOT_LOADED -> {
                        boolean b = container.map(modContainer -> !predicate.test(modContainer.getMetadata().getVersion())).orElse(true);
                        DarkMatterLog.debug("Checking mod {}. {} ({}): {}", id, state, version, b);
                        yield  b;
                    }
                };
            } catch (Exception e) {
                DarkMatterLog.error("Error while checking mods", e);
                return true;
            }
        }

        return true;
    }

}
