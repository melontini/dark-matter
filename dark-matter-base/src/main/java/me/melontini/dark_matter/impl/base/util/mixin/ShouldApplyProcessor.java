package me.melontini.dark_matter.impl.base.util.mixin;

import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.MixinShouldApply;
import me.melontini.dark_matter.api.base.util.mixin.Mod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

//TODO interfacify
public final class ShouldApplyProcessor {

    private static final String SHOULD_APPLY_DESC = "L" + MixinShouldApply.class.getName().replace(".", "/") + ";";

    private static final BiPredicate<String, Mod.Mode> MOD_PREDICATE = (s, mode) -> switch (mode) {
        case LOADED -> FabricLoader.getInstance().isModLoaded(s);
        case NOT_LOADED -> !FabricLoader.getInstance().isModLoaded(s);
    };

    private static final List<Map<String, Object>> EMPTY_ANN_ARRAY = Collections.unmodifiableList(new ArrayList<>());
    
    public static void process(AtomicBoolean bool, AnnotationNode node) {
        if (node.desc.equals(SHOULD_APPLY_DESC)) {
            Map<String, Object> values = AsmUtil.mapAnnotationNode(node);

            if (values.isEmpty()) return;

            if (!bool.get()) return;
            bool.set(checkMods(values));

            if (!bool.get()) return;
            bool.set(checkMCVersion(values));
        }
    }

    private static boolean checkMods(Map<String, Object> values) {
        List<Map<String, Object>> array = (List<Map<String, Object>>) values.getOrDefault("mods", EMPTY_ANN_ARRAY);
        if (array.isEmpty()) return true;

        for (int i = 0; i < array.size(); i += 2) {//TODO: what?
            String name = (String) array.get(i).get("value");
            Mod.Mode mode = (Mod.Mode) array.get(i).getOrDefault("mode", Mod.Mode.LOADED);

            if (!MOD_PREDICATE.test(name, mode)) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkMCVersion(Map<String, Object> values) {
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
