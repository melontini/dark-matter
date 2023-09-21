package me.melontini.dark_matter.api.config.interfaces;

import me.melontini.dark_matter.api.config.ConfigManager;

import java.lang.reflect.Field;

public interface TextEntry {

    static TextEntry literal(String literal) {
        return new TextEntry() {

            @Override
            public String get() {
                return literal;
            }

            @Override
            public Object[] args() {
                return null;
            }

            @Override
            public boolean isTranslatable() {
                return false;
            }
        };
    }

    static TextEntry translatable(String key, Object... args) {
        return new TextEntry() {

            @Override
            public String get() {
                return key;
            }

            @Override
            public Object[] args() {
                return args;
            }

            @Override
            public boolean isTranslatable() {
                return true;
            }
        };
    }

    String get();

    Object[] args();

    boolean isTranslatable();

    record InfoHolder<T>(ConfigManager<T> manager, String processor, String option, Field field) {}
}
