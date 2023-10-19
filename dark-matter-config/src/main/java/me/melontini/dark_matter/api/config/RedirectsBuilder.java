package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Redirects;
import me.melontini.dark_matter.impl.config.RedirectsImpl;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface RedirectsBuilder {

    static RedirectsBuilder create() {
        return new RedirectsImpl().builder();
    }

    RedirectsBuilder add(String from, String to);

    Redirects build();
}
