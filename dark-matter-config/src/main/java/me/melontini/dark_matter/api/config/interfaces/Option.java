package me.melontini.dark_matter.api.config.interfaces;

public interface Option {
    Object get(Object parent);
    void set(Object parent, Object value);
    String name();
    Class<?> type();
}
