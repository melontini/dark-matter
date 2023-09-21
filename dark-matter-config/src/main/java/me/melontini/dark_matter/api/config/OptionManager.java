package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.base.util.classes.Tuple;
import me.melontini.dark_matter.api.config.interfaces.Processor;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

@ApiStatus.NonExtendable
public interface OptionManager<T> {

    boolean isModified(Field f);
    boolean isModified(String option) throws NoSuchFieldException;

    void processOptions();

    Set<String> getAllProcessors();
    Optional<Processor<T>> getProcessor(String id);

    Tuple<String, Set<String>> blameProcessors(Field f);
    Set<String> blameProcessors(String option) throws NoSuchFieldException;

    Tuple<String, Set<ModContainer>> blameModJson(Field f);
    Set<ModContainer> blameModJson(String option) throws NoSuchFieldException;

    Optional<TextEntry> getReason(String processor, String option);
}
