package me.melontini.dark_matter.api.config.serializers.gson;

import com.google.gson.Gson;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import me.melontini.dark_matter.impl.config.GsonSerializer;

public class GsonSerializers {

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager) {
        return new GsonSerializer<>(manager);
    }

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager, Gson gson) {
        return new GsonSerializer<>(manager, gson);
    }

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager, Fixups fixups) {
        return new GsonSerializer<>(manager).setFixups(fixups);
    }

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager, Gson gson, Fixups fixups) {
        return new GsonSerializer<>(manager, gson).setFixups(fixups);
    }
}
