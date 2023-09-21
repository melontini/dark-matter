package me.melontini.dark_matter.api.config.interfaces;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

@FunctionalInterface
public interface ConfigClassScanner {
    void scan(Class<?> cls, Field currentField, String parentString, Set<Class<?>> recursive, List<Field> fieldRefView);
}
