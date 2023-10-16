package me.melontini.dark_matter.api.base.util.classes;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {
    void accept(T t) throws E;

    default ThrowingConsumer<T, E> andThen(ThrowingConsumer<T, E> after) {
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
