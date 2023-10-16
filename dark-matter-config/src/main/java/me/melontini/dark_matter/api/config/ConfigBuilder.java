package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.ConfigClassScanner;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import me.melontini.dark_matter.impl.config.ConfigBuilderImpl;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * All parameters outside {@link ConfigBuilder#create(Class, ModContainer, String)} are optional.
 *
 * @param <T> your desired config class
 */
@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface ConfigBuilder<T> {

    static <T> ConfigBuilder<T> create(Class<T> cls, ModContainer mod, String name) {
        return new ConfigBuilderImpl<>(cls, mod, name);
    }

    /**
     * Allows to build the config directly, instead of using reflection. Will fall back to reflection if not provided.
     */
    ConfigBuilder<T> constructor(Supplier<T> ctx);

    /**
     * The serializer used to create load and save your config. Defaults to GSON with no fixups.
     * <p>
     * Just a note, the function is called before the config is set.
     * <p>
     * {@link me.melontini.dark_matter.api.config.serializers.gson.GsonSerializers}
     */
    ConfigBuilder<T> serializer(Function<ConfigManager<T>, ConfigSerializer<T>> ctx);

    /**
     * Allows you to register redirects,
     * which will be used to redirect old options to new ones.
     * For example, if {@code enableOption} moved to {@code option.enable}
     * <p>
     * The entrypoint for this is {@code {modid}:config/{config}/redirects}
     */
    ConfigBuilder<T> redirects(Consumer<RedirectsBuilder> redirects);

    /**
     * The getter used to get option values using string names.
     */
    ConfigBuilder<T> getter(Getter<T> getter);

    /**
     * The setter used to set option values using string names.
     */
    ConfigBuilder<T> setter(Setter<T> setter);

    /**
     * Allows you to register new processors, which can be used to force set a value if some conditions are bet.
     * <p>
     * The entrypoint for this is {@code {modid}:config/{config}/processors}
     */
    ConfigBuilder<T> processors(Consumer<OptionProcessorRegistry<T>> consumer);

    ConfigBuilder<T> scanner(ConfigClassScanner scanner);

    ConfigBuilder<T> defaultReason(Function<TextEntry.InfoHolder<T>, TextEntry> reason);

    default ConfigBuilder<T> attach(Consumer<ConfigBuilder<T>> attacher) {
        attacher.accept(this);
        return this;
    }

    ConfigManager<T> build();

    interface Getter<T> {
        Object get(ConfigManager<T> configManager, String option) throws NoSuchFieldException, IllegalAccessException;
    }

    interface Setter<T> {
        void set(ConfigManager<T> manager, String option, Object value) throws NoSuchFieldException, IllegalAccessException;
    }
}
