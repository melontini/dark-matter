package me.melontini.dark_matter.api.config.serializers.gson;

import com.google.gson.JsonObject;

public interface Fixups {

    JsonObject fixup(JsonObject config);
}
