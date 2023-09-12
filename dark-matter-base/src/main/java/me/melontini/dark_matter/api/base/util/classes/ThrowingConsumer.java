package me.melontini.dark_matter.api.base.util.classes;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws Exception;
}
