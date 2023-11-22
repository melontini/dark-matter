package me.melontini.dark_matter.impl.config;

import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import me.melontini.dark_matter.api.config.interfaces.Option;

import java.lang.reflect.Field;

@ToString @EqualsAndHashCode
public class FieldOption implements Option {

    private final Field delegate;

    public FieldOption(Field delegate) {
        this.delegate = delegate;
        this.delegate.setAccessible(true);
    }

    @SneakyThrows
    @Override
    public Object get(Object parent) {
        return this.delegate.get(parent);
    }

    @SneakyThrows
    @Override
    public void set(Object parent, Object value) {
        this.delegate.set(parent, value);
    }

    @Override
    public String name() {
        return this.delegate.getName();
    }

    @Override
    public Class<?> type() {
        return this.delegate.getType();
    }
}
