package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Option;
import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface ConfigManager<T> {

    T getConfig();
    T getDefaultConfig();
    T createDefault();

    /**
     * @throws NoSuchOptionException if the option is not found
     */
    <V> V get(String option);
    default <V> V get(Class<V> type, String option) {return get(option);}
    <V> V getDefault(String option);
    default <V> V getDefault(Class<V> type, String option) {return get(option);}
    void set(String option, Object value);
    default void resetToDefault(String... options) {
        for (String option : options) {
            set(option, getDefault(option));
        }
    }

    List<Option> getFields(String option);
    default Option getField(String option) {
        List<Option> fields = getFields(option);
        return fields.get(fields.size() - 1);
    }
    String getOption(Option field);
    List<String> getOptions();

    OptionManager<T> getOptionManager();
    ConfigSerializer<T> getSerializer();

    ModContainer getMod();
    String getName();
    Class<T> getType();

    void load();
    void save();
}
