package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.interfaces.Option;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class ModJsonProcessor {

    private static final Lazy<Map<Class<?>, Function<CustomValue, ?>>> TYPES = Lazy.of(() -> () -> Utilities.consume(new HashMap<>(), map -> {
        map.put(Byte.class, element -> element.getAsNumber().byteValue());
        map.put(Short.class, element -> element.getAsNumber().shortValue());
        map.put(Integer.class, element -> element.getAsNumber().intValue());
        map.put(Long.class, element -> element.getAsNumber().longValue());
        map.put(Float.class, element -> element.getAsNumber().floatValue());
        map.put(Double.class, element -> element.getAsNumber().doubleValue());

        map.put(byte.class, element -> element.getAsNumber().byteValue());
        map.put(short.class, element -> element.getAsNumber().shortValue());
        map.put(int.class, element -> element.getAsNumber().intValue());
        map.put(long.class, element -> element.getAsNumber().longValue());
        map.put(float.class, element -> element.getAsNumber().floatValue());
        map.put(double.class, element -> element.getAsNumber().doubleValue());

        map.put(boolean.class, CustomValue::getAsBoolean);
        map.put(Boolean.class, CustomValue::getAsBoolean);

        map.put(String.class, CustomValue::getAsString);
        map.put(Character.class, element -> element.getAsString().charAt(0));
        map.put(char.class, element -> element.getAsString().charAt(0));
    }));

    final Map<String, Object> modJson = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<Option, Set<ModContainer>> modBlame = new ConcurrentHashMap<>();
    private final String json_key;
    private final ConfigManager<?> manager;
    boolean done = false;

    ModJsonProcessor(ConfigManager<?> manager) {
        this.json_key = manager.getMod().getMetadata().getId() + ":config/" + manager.getName();
        this.manager = manager;
    }

    void parseMetadata(ModContainer mod) {
        if (!mod.getMetadata().containsCustomValue(this.getKey())) return;

        CustomValue customValue = mod.getMetadata().getCustomValue(this.getKey());
        if (customValue.getType() != CustomValue.CvType.ARRAY)
            DarkMatterLog.error("{} must be an array. Mod: {} Type: {}", this.getKey(), mod.getMetadata().getId(), customValue.getType());
        else {
            CustomValue.CvArray array = customValue.getAsArray();

            for (CustomValue value : array) {
                CustomValue.CvObject object = value.getAsObject();

                if (object.containsKey("mods")) {
                    CustomValue.CvObject mods = object.get("mods").getAsObject();
                    for (Map.Entry<String, CustomValue> mod_entry : mods) {
                        if (!testModVersion(mod_entry.getKey(), mod_entry.getValue().getAsString(), mod.getMetadata().getId()))
                            return;
                    }
                }

                if (object.containsKey("values")) processValues(object.get("values").getAsObject(), mod);
            }
        }
    }

    private void processValues(CustomValue.CvObject values, ModContainer mod) {
        for (Map.Entry<String, CustomValue> feature : values) {
            Option f = manager.getField(feature.getKey());

            var converter = TYPES.get().get(f.type());
            if (converter == null) {
                DarkMatterLog.error("Unsupported {} type. Mod: {}, Type: {}", this.getKey(), mod.getMetadata().getId(), f.type());
                continue;
            }
            addModJson(mod, f, feature.getKey(), converter.apply(feature.getValue()));
        }
    }

    Set<ModContainer> blameMods(String feature) {
        return modBlame.getOrDefault(manager.getField(feature), Collections.emptySet());
    }

    Set<ModContainer> blameMods(Option field) {
        return modBlame.getOrDefault(field, Collections.emptySet());
    }

    String getKey() {
        return this.json_key;
    }

    private void addModJson(ModContainer mod, Option field, String feature, Object value) {
        modJson.put(feature, value);
        modBlame.computeIfAbsent(field, k -> new LinkedHashSet<>()).add(mod);
    }

    static boolean testModVersion(String modId, String predicate, String modBlame) {
        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(modId);
        if (mod.isPresent()) {
            try {
                VersionPredicate version = VersionPredicate.parse(predicate);
                return version.test(mod.get().getMetadata().getVersion());
            } catch (VersionParsingException e) {
                DarkMatterLog.error("Couldn't parse version predicate for {} provided by {}", modId, modBlame);
                return false;
            }
        }
        return false;
    }
}
