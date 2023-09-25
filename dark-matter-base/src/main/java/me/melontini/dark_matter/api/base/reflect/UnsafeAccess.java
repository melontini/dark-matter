package me.melontini.dark_matter.api.base.reflect;

import me.melontini.dark_matter.impl.base.reflect.UnsafeInternals;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeAccess {
    private UnsafeAccess() {
        throw new UnsupportedOperationException();
    }

    public static void putReference(Field field, Object o, Object value) {
        UnsafeInternals.setReference(field, o, value);
    }

    public static Object getReference(Field field, Object o) {
        return UnsafeInternals.getReference(field, o);
    }

    @Deprecated
    public static void putObject(Field field, Object o, Object value) {
        UnsafeInternals.setReference(field, o, value);
    }

    @Deprecated
    public static Object getObject(Field field, Object o) {
        return UnsafeInternals.getReference(field, o);
    }

    /**
     * free {@link Unsafe} for everyone!
     */
    public static Unsafe getUnsafe() {
        return UnsafeInternals.getUnsafe();
    }

    @Deprecated(forRemoval = true)
    public static @Nullable Object internalUnsafe() {
        return UnsafeInternals.internalUnsafe();
    }

    @Deprecated(forRemoval = true)
    public static long getObjectFieldOffset(Class<?> clazz, String name) {
        return UnsafeInternals.getObjectFieldOffset(clazz, name);
    }
}
