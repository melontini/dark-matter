package me.melontini.dark_matter.api.config.interfaces;

import me.melontini.dark_matter.api.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@FunctionalInterface
public interface Processor<T> {
    @Nullable Map<String, Object> process(ConfigManager<T> manager);
}
