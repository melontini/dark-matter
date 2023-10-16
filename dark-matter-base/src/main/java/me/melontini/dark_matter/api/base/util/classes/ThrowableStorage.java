package me.melontini.dark_matter.api.base.util.classes;

public class ThrowableStorage<T extends Throwable> {

    private T throwable;

    public ThrowableStorage() {
        throwable = null;
    }

    public ThrowableStorage(T throwable) {
        this.throwable = throwable;
    }

    public static <T extends Throwable> ThrowableStorage<T> of(T throwable) {
        return new ThrowableStorage<>(throwable);
    }

    public static <T extends Throwable> ThrowableStorage<T> of() {
        return new ThrowableStorage<>();
    }

    public void set(T throwable) {
        this.throwable = throwable;
    }

    public T get() {
        return throwable;
    }

    public void tryThrow() throws T {
        if (throwable != null) {
            throw throwable;
        }
    }
}
