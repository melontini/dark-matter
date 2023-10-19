package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Processor;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface OptionProcessorRegistry<T> {
    void register(String id, Processor<T> processor, ModContainer mod);

    void register(String id, Processor<T> processor, ModContainer mod, Function<TextEntry.InfoHolder<T>, TextEntry> reason);
}
