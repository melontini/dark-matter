package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.config.RedirectsBuilder;
import me.melontini.dark_matter.api.config.interfaces.Redirects;

import java.util.HashMap;
import java.util.Map;

public class RedirectsImpl implements Redirects, RedirectsBuilder {

    private final Map<String, String> redirects = new HashMap<>();

    @Override
    public boolean isEmpty() {
        return redirects.isEmpty();
    }

    @Override
    public String redirect(String from) {
        return redirects.getOrDefault(from, from);
    }

    @Override
    public RedirectsBuilder add(String from, String to) {
        this.redirects.put(from, to);
        return this;
    }

    @Override
    public Redirects build() {
        return this;
    }
}
