package me.melontini.dark_matter.api.base.util.classes;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
