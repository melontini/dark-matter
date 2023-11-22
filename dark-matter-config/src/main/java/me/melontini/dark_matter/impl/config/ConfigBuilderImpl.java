package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.config.ConfigBuilder;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.OptionProcessorRegistry;
import me.melontini.dark_matter.api.config.RedirectsBuilder;
import me.melontini.dark_matter.api.config.interfaces.ConfigClassScanner;
import me.melontini.dark_matter.api.config.interfaces.Option;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import me.melontini.dark_matter.api.config.serializers.gson.GsonSerializers;
import net.fabricmc.loader.api.ModContainer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.melontini.dark_matter.api.base.util.MakeSure.notNull;
import static me.melontini.dark_matter.api.base.util.Utilities.cast;

public class ConfigBuilderImpl<T> implements ConfigBuilder<T> {

    private final String name;
    private final Class<T> cls;
    private final ModContainer mod;

    private Function<ConfigManager<T>, ConfigSerializer<T>> serializer;
    private Supplier<T> ctx;
    private BiConsumer<OptionProcessorRegistry<T>, ModContainer> registrar;
    private ConfigClassScanner scanner;
    private Function<TextEntry.InfoHolder<T>, TextEntry> reasonFactory;

    private final RedirectsBuilder redirects = RedirectsBuilder.create();

    private Getter<T> getter;
    private Setter<T> setter;

    public ConfigBuilderImpl(Class<T> cls, ModContainer mod, String name) {
        this.name = name;
        this.cls = cls;
        this.mod = mod;
    }

    @Override
    public ConfigBuilder<T> constructor(Supplier<T> ctx) {
        this.ctx = ctx;
        return this;
    }

    @Override
    public ConfigBuilder<T> serializer(Function<ConfigManager<T>, ConfigSerializer<T>> serializer) {
        this.serializer = serializer;
        return this;
    }

    @Override
    public ConfigBuilder<T> redirects(Consumer<RedirectsBuilder> redirects) {
        redirects.accept(this.redirects);
        return this;
    }

    @Override
    public ConfigBuilder<T> getter(Getter<T> getter) {
        this.getter = getter;
        return this;
    }

    @Override
    public ConfigBuilder<T> setter(Setter<T> setter) {
        this.setter = setter;
        return this;
    }

    @Override
    public ConfigBuilder<T> processors(BiConsumer<OptionProcessorRegistry<T>, ModContainer> consumer) {
        this.registrar = consumer;
        return this;
    }

    @Override
    public ConfigBuilder<T> scanner(ConfigClassScanner scanner) {
        this.scanner = scanner;
        return this;
    }

    @Override
    public ConfigBuilder<T> defaultReason(Function<TextEntry.InfoHolder<T>, TextEntry> reason) {
        this.reasonFactory = reason;
        return this;
    }

    @Override
    public ConfigManager<T> build() {
        return new ConfigManagerImpl<>(this.cls, this.mod, this.name)
        .setupOptionManager(this.registrar, notNull(this.reasonFactory, this::defaultReason))
        .setAccessors(notNull(this.getter, this::defaultGetter), notNull(this.setter, this::defaultSetter))
        .setRedirects(this.redirects)
        .setScanner(this.scanner)
        .afterBuild(notNull(this.serializer, () -> GsonSerializers::create), notNull(this.ctx, () -> defaultCtx(this.cls)));
    }

    private static <T> Supplier<T> defaultCtx(Class<T> cls) {
        return () -> Utilities.supplyUnchecked(() -> Reflect.setAccessible(cls.getDeclaredConstructor()).newInstance());
    }

    private Function<TextEntry.InfoHolder<T>, TextEntry> defaultReason() {
        return (holder) -> TextEntry.translatable(holder.manager().getMod().getMetadata().getId() + ".config.option_manager.reason." + holder.processor().id().replace(':', '.'));
    }

    private Getter<T> defaultGetter() {
        return (context, option) -> Utilities.supplyUnchecked(() -> {
            List<Option> fields = context.manager().getFields(option);
            Object obj = context.config();
            for (Option field : fields) {
                obj = field.get(obj);
            }
            return cast(obj);
        });
    }

    private Setter<T> defaultSetter() {
        return (context, option, value) -> Utilities.runUnchecked(() -> {
            List<Option> fields = context.manager().getFields(option);
            Object obj = context.config();
            for (int i = 0; i < fields.size() - 1; i++) {
                Option field = fields.get(i);
                obj = field.get(obj);
            }
            Option f = fields.get(fields.size() - 1);
            f.set(obj, value);
        });
    }
}
