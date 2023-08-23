package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.IPluginPlugin;
import me.melontini.dark_matter.api.base.util.mixin.MixinShouldApply;
import me.melontini.dark_matter.api.base.util.mixin.Mod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

@ApiStatus.Internal
public final class ShouldApplyPlugin implements IPluginPlugin {

    private static final String SHOULD_APPLY_DESC = "L" + MixinShouldApply.class.getName().replace(".", "/") + ";";

    private static final BiPredicate<String, Mod.Mode> MOD_PREDICATE = (s, mode) -> switch (mode) {
        case LOADED -> FabricLoader.getInstance().isModLoaded(s);
        case NOT_LOADED -> !FabricLoader.getInstance().isModLoaded(s);
    };

    private static final List<Map<String, Object>> EMPTY_ANN_ARRAY = Collections.unmodifiableList(new ArrayList<>());

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName, ClassNode mixinNode, List<AnnotationNode> mergedAnnotations) {
        Optional<AnnotationNode> optional = mergedAnnotations.stream().filter(node -> node.desc.equals(SHOULD_APPLY_DESC)).findFirst();

        AtomicBoolean apply = new AtomicBoolean(true);
        optional.ifPresent(node -> process(apply, node));

        return apply.get();
    }

    @Override
    public void afterApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (targetClass.visibleAnnotations != null && !targetClass.visibleAnnotations.isEmpty()) {
            targetClass.visibleAnnotations.removeIf(node -> SHOULD_APPLY_DESC.equals(node.desc));
        }
    }

    private static void process(AtomicBoolean bool, AnnotationNode node) {
        Map<String, Object> values = AsmUtil.mapAnnotationNode(node);

        if (values.isEmpty()) return;

        if (!bool.get()) return;
        bool.set(checkMods(values));

        if (!bool.get()) return;
        bool.set(checkMCVersion(values));
    }

    private static boolean checkMods(@NotNull Map<String, Object> values) {
        List<Map<String, Object>> array = (List<Map<String, Object>>) values.getOrDefault("mods", EMPTY_ANN_ARRAY);
        if (array.isEmpty()) return true;

        for (Map<String, Object> map : array) {
            String  name = (String) map.get("value");
            Mod.Mode mode = (Mod.Mode) map.getOrDefault("mode", Mod.Mode.LOADED);

            if (!MOD_PREDICATE.test(name, mode)) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkMCVersion(@NotNull Map<String, Object> values) {
        String version = (String) values.getOrDefault("mcVersion", "");

        if (version.isEmpty()) return true;

        try {
            var predicate = VersionPredicate.parse(version);
            return predicate.test(FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getMetadata().getVersion());
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

}