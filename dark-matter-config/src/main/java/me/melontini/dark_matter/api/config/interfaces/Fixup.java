package me.melontini.dark_matter.api.config.interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Fixup {
    boolean fixup(InfoHolder holder);

    record InfoHolder(JsonObject config, @Nullable JsonObject parent, JsonElement value, String[] key) {}
}
