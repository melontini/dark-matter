package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.base.util.classes.Tuple;
import me.melontini.dark_matter.api.config.interfaces.Option;
import me.melontini.dark_matter.api.config.interfaces.Processor;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface OptionManager<T> {

    boolean isModified(Option f);
    boolean isModified(String option);

    void processOptions();

    Collection<ProcessorEntry<T>> getAllProcessors();
    Optional<ProcessorEntry<T>> getProcessor(String id);

    Tuple<String, Set<ProcessorEntry<T>>> blameProcessors(Option f);
    Set<ProcessorEntry<T>> blameProcessors(String option);

    Tuple<String, Set<ModContainer>> blameModJson(Option f);
    Set<ModContainer> blameModJson(String option);

    Optional<TextEntry> getReason(String processor, String option);

    ConfigManager<T> getConfigManager();

    record ProcessorEntry<T>(String id, Processor<T> processor, ModContainer mod) { }
}
