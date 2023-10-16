package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.base.util.PrependingLogger;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import me.melontini.dark_matter.api.base.util.classes.Tuple;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.OptionManager;
import me.melontini.dark_matter.api.config.OptionProcessorRegistry;
import me.melontini.dark_matter.api.config.interfaces.Processor;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class OptionManagerImpl<T> implements OptionManager<T>, OptionProcessorRegistry<T> {

    private final Map<String, OptionProcessorEntry<T>> optionProcessors = new LinkedHashMap<>();
    private final ConfigManager<T> manager;
    private final Function<TextEntry.InfoHolder<T>, TextEntry> defaultReason;
    private final Map<String, Function<TextEntry.InfoHolder<T>, TextEntry>> customReasons = new HashMap<>();
    private final Lazy<PrependingLogger> logger;

    private final Map<Field, Set<String>> modifiedFields = new HashMap<>();

    private final ModJsonProcessor modJsonProcessor;

    OptionManagerImpl(ConfigManager<T> manager, Function<TextEntry.InfoHolder<T>, TextEntry> defaultReason) {
        this.manager = manager;
        this.modJsonProcessor = new ModJsonProcessor(manager);
        this.defaultReason = defaultReason;
        this.logger = Lazy.of(() -> () -> PrependingLogger.get(manager.getMod().getMetadata().getName() + "/OptionManager"));

        register(manager.getMod().getMetadata().getId() + ":custom_values", manager1 -> {
            if (!this.modJsonProcessor.done) {
                FabricLoader.getInstance().getAllMods().stream()
                        .filter(container -> container.getMetadata().containsCustomValue(this.modJsonProcessor.getKey()))
                        .forEach(this.modJsonProcessor::parseMetadata);
                this.modJsonProcessor.done = true;
            }
            return this.modJsonProcessor.modJson;
        }, holder -> TextEntry.translatable("dark-matter.config.option_manager.reason.custom_values",
                Arrays.toString(holder.manager().getOptionManager().blameModJson(holder.field()).right().stream()
                        .map(container -> container.getMetadata().getName()).toArray())));
    }

    @Override
    public void processOptions() {
        this.optionProcessors.forEach((key, entry) -> {
            var config = entry.processor().process(this.manager);
            if (config != null && !config.isEmpty()) {

                this.logger.get().info("Processor: {}", key);
                StringBuilder builder = new StringBuilder().append("Config: ");
                config.keySet().forEach(s -> builder.append(s).append("=").append(config.get(s)).append("; "));
                this.logger.get().info(builder.toString());

                configure(key, config);
            }
        });
    }

    @Override
    public Set<String> getAllProcessors() {
        return Collections.unmodifiableSet(this.optionProcessors.keySet());
    }

    @Override
    public Optional<Processor<T>> getProcessor(String id) {
        return Optional.ofNullable(this.optionProcessors.get(id).processor());
    }

    private void configure(String id, Map<String, Object> config) {
        validateId(id);

        config.forEach((s, o) -> {
            try {
                this.manager.set(s, o);
                Field f = this.manager.getField(s);
                this.modifiedFields.computeIfAbsent(f, field -> new HashSet<>()).add(id);
            } catch (NoSuchFieldException e) {
                DarkMatterLog.error("Option %s does not exist (%s)".formatted(s, id), e);
            }
        });
    }

    static void validateId(String id) {
        String[] split = id.split(":");
        if (split.length != 2) throw new IllegalArgumentException("Invalid id: " + id);
    }

    @Override
    public boolean isModified(Field f) {
        return this.modifiedFields.containsKey(f);
    }

    @Override
    public boolean isModified(String option) throws NoSuchFieldException {
        return isModified(this.manager.getField(option));
    }

    @Override
    public Tuple<String, Set<String>> blameProcessors(Field f) {
        return Tuple.of(this.manager.getOption(f), this.modifiedFields.getOrDefault(f, Collections.emptySet()));
    }

    @Override
    public Set<String> blameProcessors(String option) throws NoSuchFieldException {
        return this.modifiedFields.getOrDefault(this.manager.getField(option), Collections.emptySet());
    }

    @Override
    public Tuple<String, Set<ModContainer>> blameModJson(Field f) {
        return Tuple.of(this.manager.getOption(f), this.modJsonProcessor.blameMods(f));
    }

    @Override
    public Set<ModContainer> blameModJson(String option) throws NoSuchFieldException {
        return this.modJsonProcessor.blameMods(option);
    }

    @Override
    public Optional<TextEntry> getReason(String processor, String option) {
        try {
            return Optional.ofNullable(this.customReasons.getOrDefault(processor, this.defaultReason).apply(new TextEntry.InfoHolder<>(this.manager, processor, option, this.manager.getField(option))));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    @Override
    public ConfigManager<T> getConfigManager() {
        return this.manager;
    }

    @Override
    public void register(String id, Processor<T> processor) {
        validateId(id);
        var last = this.optionProcessors.put(id, new OptionProcessorEntry<>(id, processor));
        if (last != null) throw new IllegalStateException("Tried to register an option processor with the same id (%s) twice!".formatted(id));
    }

    @Override
    public void register(String id, Processor<T> processor, Function<TextEntry.InfoHolder<T>, TextEntry> reason) {
        register(id, processor);
        this.customReasons.put(id, reason);
    }

    private record OptionProcessorEntry<T>(String id, Processor<T> processor) {

    }
}
