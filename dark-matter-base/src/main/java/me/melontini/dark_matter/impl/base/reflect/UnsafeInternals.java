package me.melontini.dark_matter.impl.base.reflect;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

@UtilityClass
@ApiStatus.Internal
public class UnsafeInternals {

    private static final Lazy<Unsafe> UNSAFE = Lazy.of(() -> () -> {
        try {
            Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
            unsafe.setAccessible(true);
            return (Unsafe) unsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Constructor<Unsafe> constructor = Unsafe.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException ex) {
                throw new RuntimeException("Couldn't access Unsafe", ex);
            }
        }
    });

    public static void setReference(Field field, Object o, Object value) {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            getUnsafe().putObjectVolatile(
                    isStatic ? getUnsafe().staticFieldBase(field) : o,
                    isStatic ? getUnsafe().staticFieldOffset(field) : getUnsafe().objectFieldOffset(field),
                    value);
        } else {
            getUnsafe().putObject(
                    isStatic ? getUnsafe().staticFieldBase(field) : o,
                    isStatic ? getUnsafe().staticFieldOffset(field) : getUnsafe().objectFieldOffset(field),
                    value);
        }
    }

    public static <T> T getReference(Field field, Object o) {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            return Utilities.cast(getUnsafe().getObjectVolatile(
                    isStatic ? getUnsafe().staticFieldBase(field) : o,
                    isStatic ? getUnsafe().staticFieldOffset(field) : getUnsafe().objectFieldOffset(field)));
        } else {
            return Utilities.cast(getUnsafe().getObject(
                    isStatic ? getUnsafe().staticFieldBase(field) : o,
                    isStatic ? getUnsafe().staticFieldOffset(field) : getUnsafe().objectFieldOffset(field)));
        }
    }

    public static <T> T allocateInstance(Class<T> cls) throws InstantiationException {
        return Utilities.cast(getUnsafe().allocateInstance(cls));
    }

    public static Unsafe getUnsafe() {
        return UNSAFE.get();
    }

    @Deprecated(forRemoval = true)
    private static final Lazy<Object> internalUnsafe = Lazy.of(() -> () -> getReference(Unsafe.class.getDeclaredField("theInternalUnsafe"), null));

    @Deprecated(forRemoval = true)
    public static @Nullable Object internalUnsafe() {
        return internalUnsafe.get();
    }

    @Deprecated(forRemoval = true)
    private static final Lazy<MethodHandle> objectFieldOffset = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findVirtual(internalUnsafe().getClass(), "objectFieldOffset", MethodType.methodType(long.class, Class.class, String.class)));

    @Deprecated(forRemoval = true)
    public static long getObjectFieldOffset(Class<?> clazz, String name) {
        try {
            return (long) objectFieldOffset.get().invokeWithArguments(internalUnsafe(), clazz, name);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
