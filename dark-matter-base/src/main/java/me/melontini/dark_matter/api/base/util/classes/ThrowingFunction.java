package me.melontini.dark_matter.api.base.util.classes;

public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}
