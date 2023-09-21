package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Redirects;
import me.melontini.dark_matter.impl.config.RedirectsImpl;


public interface RedirectsBuilder {

    static RedirectsBuilder create() {
        return new RedirectsImpl();
    }

    RedirectsBuilder add(String from, String to);

    Redirects build();
}
