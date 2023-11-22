package me.melontini.dark_matter.impl.config.serializers.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.config.serializers.gson.Fixup;
import me.melontini.dark_matter.api.config.serializers.gson.Fixups;
import me.melontini.dark_matter.api.config.serializers.gson.FixupsBuilder;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FixupsImpl implements Fixups {

    private final Map<String[], Set<Fixup>> fixups = Collections.synchronizedMap(new HashMap<>());

    public FixupsBuilder builder() {
        return new FixupsBuilder() {
            @Override
            public FixupsBuilder add(String key, Fixup fixup) {
                FixupsImpl.this.fixups.computeIfAbsent(key.split("\\."), k -> Collections.synchronizedSet(new HashSet<>())).add(fixup);
                return this;
            }

            @Override
            public Fixups build() {
                return FixupsImpl.this;
            }
        };
    }

    @Override
    public JsonObject fixup(JsonObject config) {
        for (Map.Entry<String[], Set<Fixup>> entry : fixups.entrySet()) {
            JsonElement e = config;
            JsonObject parent = null;
            for (String key : entry.getKey())
                if (e instanceof JsonObject o) {
                    parent = o;
                    e = o.get(key);
                }
            if (e == null || e.isJsonNull()) continue;

            Fixup.InfoHolder holder = new Fixup.InfoHolder(config, parent, e, entry.getKey());
            entry.getValue().forEach(fixup -> {
                if (fixup.fixup(holder))
                    DarkMatterLog.debug("Fixed-up config entry %s".formatted(StringUtils.joinWith(".", (Object[]) entry.getKey())));
            });
        }
        return config;
    }
}
