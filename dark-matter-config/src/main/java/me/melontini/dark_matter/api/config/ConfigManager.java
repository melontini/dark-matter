package me.melontini.dark_matter.api.config;

import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

@ApiStatus.NonExtendable
public interface ConfigManager<T> {

    T getConfig();
    Reference<T> getConfigRef();
    T getDefaultConfig();

    <V> V get(String option) throws NoSuchFieldException;
    default <V> V get(@SuppressWarnings("unused") Class<V> type, String option) throws NoSuchFieldException {
        return get(option);
    }
    void set(String option, Object value) throws NoSuchFieldException;
    List<Field> getFields(String option) throws NoSuchFieldException;
    default Field getField(String option) throws NoSuchFieldException {
        List<Field> fields = getFields(option);
        return fields.get(fields.size() - 1);
    }
    String getOption(Field field);
    List<String> getOptions();

    OptionManager<T> getOptionManager();

    ModContainer getMod();
    String getName();
    Path getPath();

    void load();
    void save();

    interface Reference<T> {
        T get();
    }
}
