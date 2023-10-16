package me.melontini.dark_matter.impl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import me.melontini.dark_matter.api.config.serializers.gson.Fixups;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class GsonSerializer<T> implements ConfigSerializer<T> {

    private final ConfigManager<T> manager;
    private final Path configPath;
    private final Gson gson;
    private Function<JsonObject, JsonObject> fixupFunc = Function.identity();

    public GsonSerializer(ConfigManager<T> manager, Gson gson) {
        this.manager = manager;
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(manager.getName() + ".json");
        this.gson = gson;
    }

    public GsonSerializer(ConfigManager<T> manager) {
        this(manager, new GsonBuilder().setPrettyPrinting().create());
    }

    public GsonSerializer<T> setFixups(Fixups fixups) {
        this.fixupFunc = fixups::fixup;
        return this;
    }

    @Override
    public T load() {
        if (Files.exists(this.getPath())) {
            try (var reader = Files.newBufferedReader(this.getPath())) {
                JsonObject object = this.fixupFunc.apply(JsonParser.parseReader(reader).getAsJsonObject());

                return this.gson.fromJson(object, this.manager.getType());
            } catch (IOException e) {
                DarkMatterLog.error("Failed to load {}, using defaults", this.getPath());
            }
        }
        return this.manager.createDefault();
    }

    @Override
    public void save() {
        try {
            Files.createDirectories(this.getPath().getParent());
            Files.write(this.getPath(), this.gson.toJson(this.manager.getConfig()).getBytes());
        } catch (Exception e) {
            DarkMatterLog.error("Failed to save {}", this.getPath(), e);
        }
    }

    @Override
    public Path getPath() {
        return this.configPath;
    }

    @Override
    public ConfigManager<T> getConfigManager() {
        return this.manager;
    }
}
