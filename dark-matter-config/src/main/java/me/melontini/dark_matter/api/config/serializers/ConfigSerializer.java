package me.melontini.dark_matter.api.config.serializers;

import me.melontini.dark_matter.api.config.ConfigManager;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

public interface ConfigSerializer<T> {

    @ApiStatus.OverrideOnly
    T load();
    @ApiStatus.OverrideOnly
    void save();

    Path getPath();
    ConfigManager<T> getConfigManager();
}
