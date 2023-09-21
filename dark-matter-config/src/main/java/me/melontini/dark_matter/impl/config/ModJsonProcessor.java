package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class ModJsonProcessor {

    private static final Map<Class<?>, Function<CustomValue, ?>> TYPES = Utilities.consume(new HashMap<>(), map -> {
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
    });

    final Map<String, Object> modJson = new LinkedHashMap<>();
    private final Map<Field, Set<ModContainer>> modBlame = new HashMap<>();
    private final String json_key;
    private final ConfigManager<?> manager;
    boolean done = false;

    ModJsonProcessor(ConfigManager<?> manager) {
        this.json_key = manager.getMod().getMetadata().getId() + ":" + manager.getName() + "-config";
        this.manager = manager;
    }

    void parseMetadata(ModContainer mod) {
        if (!mod.getMetadata().containsCustomValue(this.json_key)) return;

        CustomValue customValue = mod.getMetadata().getCustomValue(this.json_key);
        if (customValue.getType() != CustomValue.CvType.ARRAY)
            DarkMatterLog.error("{} must be an array. Mod: {} Type: {}", this.json_key, mod.getMetadata().getId(), customValue.getType());
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
            Field f;
            try {
                f = manager.getField(feature.getKey());
            } catch (NoSuchFieldException e) {
                DarkMatterLog.error("Couldn't find option {}. Mod: {}", feature.getKey(), mod.getMetadata().getId());
                continue;
            }

            var converter = TYPES.get(f.getType());
            if (converter == null) {
                DarkMatterLog.error("Unsupported {} type. Mod: {}, Type: {}", this.json_key, mod.getMetadata().getId(), f.getType());
                continue;
            }
            addModJson(mod, f, feature.getKey(), converter.apply(feature.getValue()));
        }
    }

    Set<ModContainer> blameMods(String feature) throws NoSuchFieldException {
        return modBlame.getOrDefault(manager.getField(feature), Collections.emptySet());
    }

    Set<ModContainer> blameMods(Field field) {
        return modBlame.getOrDefault(field, Collections.emptySet());
    }

    String getKey() {
        return this.json_key;
    }

    private void addModJson(ModContainer mod, Field field, String feature, Object value) {
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
