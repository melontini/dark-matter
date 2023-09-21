package me.melontini.dark_matter.api.config.interfaces;

import com.google.gson.JsonObject;

public interface Fixups {

    JsonObject fixup(JsonObject config);

    boolean isEmpty();
}
