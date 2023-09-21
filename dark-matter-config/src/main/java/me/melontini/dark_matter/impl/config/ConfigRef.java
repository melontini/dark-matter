package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.config.ConfigManager;

class ConfigRef<T> implements ConfigManager.Reference<T> {

    volatile T value;

    @Override
    public T get() {
        return this.value;
    }

    void set(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
