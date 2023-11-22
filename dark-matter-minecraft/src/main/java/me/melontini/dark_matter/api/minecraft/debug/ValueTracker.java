package me.melontini.dark_matter.api.minecraft.debug;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.minecraft.debug.ValueTrackerImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.function.Supplier;

@UtilityClass
@ApiStatus.Experimental
@SuppressWarnings("unused")
public final class ValueTracker {

    /**
     * This method is used to track anything using a supplier.
     * <p>
     * It's recommended to return strings or objects that implement the toString() method.
     * <p>
     * The supplier cannot be null, but can return null.
     * @param s unique id/name for the tracker.
     */
    public static void addTracker(@NotNull String s, @NotNull Supplier<?> supplier) {
        ValueTrackerImpl.addTracker(s, supplier);
    }

    /**
     * This method is used to track anything using a supplier, but you can specify a duration.
     * <p>
     * Once the time is up, the tracker will be removed.
     * <p>
     * The supplier cannot be null, but can return null.
     * @param s unique id/name for the tracker.
     */
    public static void addTracker(@NotNull String s, @NotNull Supplier<?> supplier, @NotNull Duration duration) {
        ValueTrackerImpl.addTracker(s, supplier, duration);
    }

    /**
     * This method is used to add a tracker for an object field.
     * @param s unique id/name for the tracker.
     */
    public static void addTracker(@NotNull String s, @NotNull Field f, @NotNull Object o) {
        ValueTrackerImpl.addFieldTracker(s, f, o);
    }

    /**
     * This method is used to add a tracker for an object field, but you can specify a duration.
     * <p>
     * Once the time is up, the tracker will be removed.
     * @param s unique id/name for the tracker.
     */
    public static void addTracker(@NotNull String s, @NotNull Field f, @NotNull Object o, @NotNull Duration duration) {
        ValueTrackerImpl.addFieldTracker(s, f, o, duration);
    }

    /**
     * This method is used to add a tracker for a static field.
     * @param s unique id/name for the tracker.
     */
    public static void addTracker(@NotNull String s, @NotNull Field f) {
        ValueTrackerImpl.addStaticFieldTracker(s, f);
    }

    /**
     * This method is used to add a tracker for a static field, but you can specify a duration.
     * <p>
     * Once the time is up, the tracker will be removed.
     * @param s unique id/name for the tracker.
     */
    public static void addTracker(@NotNull String s, @NotNull Field f, @NotNull Duration duration) {
        ValueTrackerImpl.addStaticFieldTracker(s, f, duration);
    }

    public static void removeTracker(@NotNull String s) {
        ValueTrackerImpl.removeTracker(s);
    }
}
