package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.List;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface ConfigManager<T> {

    T getConfig();
    T getDefaultConfig();
    T createDefault();

    <V> V get(String option) throws NoSuchFieldException;
    default <V> V get(Class<V> type, String option) throws NoSuchFieldException {
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
    ConfigSerializer<T> getSerializer();

    ModContainer getMod();
    String getName();
    Class<T> getType();

    void load();
    void save();
}
