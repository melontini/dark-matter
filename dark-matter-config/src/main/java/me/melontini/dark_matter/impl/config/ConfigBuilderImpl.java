package me.melontini.dark_matter.impl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.config.*;
import me.melontini.dark_matter.api.config.interfaces.Fixups;
import me.melontini.dark_matter.api.config.interfaces.Redirects;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.lang.reflect.Field;
import java.util.function.Consumer;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

public class ConfigBuilderImpl<T> implements ConfigBuilder<T> {

    private final String name;
    private final Class<T> cls;
    private final ModContainer mod;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Fixups fixups = null;
    private Redirects redirects = null;

    private Getter<T> getter = (manager, option) -> {
        Object obj = manager.getConfig();
        for (String s : option.split("\\.")) {
            Field field = obj.getClass().getDeclaredField(s);
            field.setAccessible(true);
            obj = field.get(obj);
        }
        return cast(obj);
    };
    private Setter<T> setter = (manager, option, value) -> {
        Object obj = manager.getConfig();
        String[] split = option.split("\\.");
        for (int i = 0; i < split.length - 1; i++) {
            Field field = obj.getClass().getDeclaredField(split[i]);
            field.setAccessible(true);
            obj = field.get(obj);
        }
        Field f = obj.getClass().getDeclaredField(split[split.length - 1]);
        f.setAccessible(true);
        f.set(obj, value);
    };

    public ConfigBuilderImpl(Class<T> cls, ModContainer mod, String name) {
        this.name = name;
        this.cls = cls;
        this.mod = mod;
    }

    @Override
    public ConfigBuilderImpl<T> fixups(FixupsBuilder fixups) {
        FabricLoader.getInstance().getObjectShare().put(getShareId("fixups"), fixups);
        this.fixups = fixups.build();
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> redirects(RedirectsBuilder redirects) {
        FabricLoader.getInstance().getObjectShare().put(getShareId("redirects"), redirects);
        this.redirects = redirects.build();
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> getter(Getter<T> getter) {
        this.getter = getter;
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> setter(Setter<T> setter) {
        this.setter = setter;
        return this;
    }

    public ConfigBuilderImpl<T> gson(Gson gson) {
        this.gson = gson;
        return this;
    }

    @Override
    public ConfigBuilder<T> processors(Consumer<OptionProcessorRegistry<T>> consumer) {
        FabricLoader.getInstance().getObjectShare().whenAvailable(getShareId("processors"), (s, o) -> {
            if (o instanceof OptionProcessorRegistry<?> registry) {
                consumer.accept(Utilities.cast(registry));
            }
        });
        return this;
    }

    @Override
    public ConfigManager<T> build() {
        return new ConfigManagerImpl<>(this.cls, this.mod, this.name, this.gson,
                this.fixups, this.redirects, this.getter, this.setter);
    }

    private String getShareId(String key) {
        return this.mod.getMetadata().getId() + ":config-" + key + "-" + this.name;
    }
}