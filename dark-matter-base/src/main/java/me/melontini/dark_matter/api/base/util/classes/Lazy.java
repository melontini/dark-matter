package me.melontini.dark_matter.api.base.util.classes;

import lombok.SneakyThrows;
import me.melontini.dark_matter.api.base.util.MakeSure;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Lazy<T> {

    private Supplier<Callable<T>> supplier;
    private T value;

    public Lazy(Supplier<Callable<T>> supplier) {
        this.supplier = MakeSure.notNull(supplier);
    }

    public static <T> Lazy<T> of(Supplier<Callable<T>> supplier) {
        return new Lazy<>(supplier);
    }

    public boolean isInitialized() {
        return supplier == null;
    }

    @SneakyThrows
    public T get() {
        return getExc();
    }

    public T getExc() throws Exception {
        if (value == null && supplier != null) {
            value = supplier.get().call();
            supplier = null;
        }
        return value;
    }
}
