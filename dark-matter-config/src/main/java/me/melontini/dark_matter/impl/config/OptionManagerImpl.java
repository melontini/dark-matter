package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.base.util.PrependingLogger;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import me.melontini.dark_matter.api.base.util.classes.Tuple;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.NoSuchOptionException;
import me.melontini.dark_matter.api.config.OptionManager;
import me.melontini.dark_matter.api.config.OptionProcessorRegistry;
import me.melontini.dark_matter.api.config.interfaces.Option;
import me.melontini.dark_matter.api.config.interfaces.Processor;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class OptionManagerImpl<T> implements OptionManager<T>, OptionProcessorRegistry<T> {

    private final Map<String, ProcessorEntry<T>> optionProcessors = Collections.synchronizedMap(new LinkedHashMap<>());
    private final ConfigManager<T> manager;
    private final Function<TextEntry.InfoHolder<T>, TextEntry> defaultReason;
    private final Map<String, Function<TextEntry.InfoHolder<T>, TextEntry>> customReasons = new ConcurrentHashMap<>();
    private final Lazy<PrependingLogger> logger;

    private final Map<Option, Set<ProcessorEntry<T>>> modifiedFields = new ConcurrentHashMap<>();

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
        }, manager.getMod(), holder -> TextEntry.translatable("dark-matter.config.option_manager.reason.custom_values",
                Arrays.toString(holder.manager().getOptionManager().blameModJson(holder.field()).right().stream()
                        .map(container -> container.getMetadata().getName()).toArray())));
    }

    @Override
    public void processOptions() {
        this.modifiedFields.clear();
        this.optionProcessors.forEach((key, entry) -> {
            var config = entry.processor().process(this.getConfigManager());
            if (config != null && !config.isEmpty()) {

                this.logger.get().debug("Processor: {}", key);
                StringBuilder builder = new StringBuilder().append("Config: ");
                config.keySet().forEach(s -> builder.append(s).append("=").append(config.get(s)).append("; "));
                this.logger.get().debug(builder.toString());

                configure(entry, config);
            }
        });
    }

    @Override
    public Collection<ProcessorEntry<T>> getAllProcessors() {
        return Collections.unmodifiableCollection(this.optionProcessors.values());
    }

    @Override
    public Optional<ProcessorEntry<T>> getProcessor(String id) {
        return Optional.ofNullable(this.optionProcessors.get(id));
    }

    private void configure(ProcessorEntry<T> entry, Map<String, Object> config) {
        validateId(entry.id());

        config.forEach((s, o) -> {
            try {
                this.getConfigManager().set(s, o);
                Option f = this.getConfigManager().getField(s);
                this.modifiedFields.computeIfAbsent(f, field -> new HashSet<>()).add(entry);
            } catch (NoSuchOptionException e) {
                DarkMatterLog.error("Option %s does not exist (%s)".formatted(s, entry.id()), e);
            }
        });
    }

    static void validateId(String id) {
        String[] split = id.split(":");
        if (split.length != 2) throw new IllegalArgumentException("Invalid id: " + id);
    }

    @Override
    public boolean isModified(Option f) {
        return this.modifiedFields.containsKey(f);
    }

    @Override
    public boolean isModified(String option) {
        return isModified(this.getConfigManager().getField(option));
    }

    @Override
    public Tuple<String, Set<ProcessorEntry<T>>> blameProcessors(Option f) {
        return Tuple.of(this.getConfigManager().getOption(f), Collections.unmodifiableSet(this.modifiedFields.getOrDefault(f, Collections.emptySet())));
    }

    @Override
    public Set<ProcessorEntry<T>> blameProcessors(String option) {
        return Collections.unmodifiableSet(this.modifiedFields.getOrDefault(this.getConfigManager().getField(option), Collections.emptySet()));
    }

    @Override
    public Tuple<String, Set<ModContainer>> blameModJson(Option f) {
        return Tuple.of(this.getConfigManager().getOption(f), Collections.unmodifiableSet(this.modJsonProcessor.blameMods(f)));
    }

    @Override
    public Set<ModContainer> blameModJson(String option) {
        return Collections.unmodifiableSet(this.modJsonProcessor.blameMods(option));
    }

    @Override
    public Optional<TextEntry> getReason(String processor, String option) {
        try {
            return Optional.ofNullable(this.customReasons.getOrDefault(processor, this.defaultReason).apply(new TextEntry.InfoHolder<>(this.getConfigManager(), this.getProcessor(processor).orElseThrow(() -> new NoSuchFieldException("processor: " + processor)), option, this.getConfigManager().getField(option))));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    @Override
    public ConfigManager<T> getConfigManager() {
        return this.manager;
    }

    @Override
    public void register(String id, Processor<T> processor, ModContainer mod) {
        validateId(id);
        var last = this.optionProcessors.put(id, new ProcessorEntry<>(id, processor, mod));
        if (last != null) throw new IllegalStateException("Tried to register an option processor with the same id (%s) twice!".formatted(id));
    }

    @Override
    public void register(String id, Processor<T> processor, ModContainer mod, Function<TextEntry.InfoHolder<T>, TextEntry> reason) {
        register(id, processor, mod);
        this.customReasons.put(id, reason);
    }
}
