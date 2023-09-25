package me.melontini.dark_matter.impl.base.reflect;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

@ApiStatus.Internal
public class UnsafeInternals {
    private UnsafeInternals() {
        throw new UnsupportedOperationException();
    }

    private static final Unsafe UNSAFE = Utilities.supply(() -> {
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
        o = Modifier.isStatic(field.getModifiers()) ? UNSAFE.staticFieldBase(field) : o;
        long l = Modifier.isStatic(field.getModifiers()) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            UNSAFE.putObjectVolatile(o, l, value);
        } else {
            UNSAFE.putObject(o, l, value);
        }
    }

    public static Object getReference(Field field, Object o) {
        o = Modifier.isStatic(field.getModifiers()) ? UNSAFE.staticFieldBase(field) : o;
        long l = Modifier.isStatic(field.getModifiers()) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            return UNSAFE.getObjectVolatile(o, l);
        } else {
            return UNSAFE.getObject(o, l);
        }
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    private static Object internalUnsafe;

    @Deprecated
    public static @Nullable Object internalUnsafe() {
        if (internalUnsafe == null) {
            try {
                Field f2 = Unsafe.class.getDeclaredField("theInternalUnsafe");
                internalUnsafe = getReference(f2, null);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't access internal Unsafe", e);
            }
        }
        return internalUnsafe;
    }

    private static MethodHandle objectFieldOffset;

    @Deprecated
    public static long getObjectFieldOffset(Class<?> clazz, String name) {
        try {
            if (objectFieldOffset == null) {
                objectFieldOffset = ReflectionInternals.stealTrustedLookup()
                        .findVirtual(internalUnsafe().getClass(), "objectFieldOffset", MethodType.methodType(long.class, Class.class, String.class));
            }
            return (long) objectFieldOffset.invokeWithArguments(internalUnsafe(), clazz, name);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static int offset = -1;

    //https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass
    public static int getOverrideOffset() {
        if (offset == -1) {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe"), f1 = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                f1.setAccessible(false);
                Unsafe unsafe = (Unsafe) f.get(null);
                int i;//override boolean byte offset. should result in 12 for java 17
                for (i = 0; unsafe.getBoolean(f, i) == unsafe.getBoolean(f1, i); i++) ;
                offset = i;
            } catch (Exception ignored) {
                offset = 12; //fallback to 12 just in case
            }
        }
        MakeSure.isTrue(offset != -1);
        return offset;
    }
}
