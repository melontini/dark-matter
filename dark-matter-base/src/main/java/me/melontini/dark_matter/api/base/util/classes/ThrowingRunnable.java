package me.melontini.dark_matter.api.base.util.classes;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {
    void run() throws E;
}
