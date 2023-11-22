package me.melontini.dark_matter.api.base.reflect;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import me.melontini.dark_matter.impl.base.reflect.MiscReflectionInternals;
import me.melontini.dark_matter.impl.base.reflect.ReflectionInternals;
import me.melontini.dark_matter.impl.base.reflect.UnsafeInternals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass">source</a>
 */
@UtilityClass
@SuppressWarnings("unused")
@Deprecated
public class ReflectionUtil {

    /**
     * See {@link Reflect#findConstructor(Class, Object...)}
     */
    public static @Nullable <T> Constructor<T> findConstructor(Class<T> clazz, Object... args) {
        return Reflect.findConstructor(clazz, args).orElse(null);
    }

    /**
     * See {@link Reflect#findConstructor(Class, List)}
     */
    public static @Nullable <T> Constructor<T> findConstructor(@NotNull Class<T> clazz, List<Object> args) {
        return Reflect.findConstructor(clazz, args).orElse(null);
    }

    /**
     * See {@link Reflect#findMethod(Class, String, Object...)}
     */
    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, Object... args) {
        return Reflect.findMethod(clazz, name, args).orElse(null);
    }

    /**
     * See {@link Reflect#findMethod(Class, String, List)}
     */
    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, List<Object> args) {
        return Reflect.findMethod(clazz, name, args).orElse(null);
    }

    public static @Nullable <T> Field findField(@NotNull Class<T> clazz, String name) {
        return Reflect.findField(clazz, name).orElse(null);
    }

    /**
     * See {@link Reflect#setAccessible(AccessibleObject)}
     */
    public static <T> Constructor<T> setAccessible(Constructor<T> constructor) {
        return Reflect.setAccessible(constructor, true);
    }

    /**
     * See {@link Reflect#setAccessible(AccessibleObject)}
     */
    public static Method setAccessible(Method method) {
        return Reflect.setAccessible(method, true);
    }

    /**
     * See {@link Reflect#setAccessible(AccessibleObject)}
     */
    public static Field setAccessible(Field field) {
        return Reflect.setAccessible(field, true);
    }

    @Deprecated(forRemoval = true)
    public static void addOpensOrExports(Module module, String pn, Module other, boolean open, boolean syncVM) {
        try {
            MiscReflectionInternals.addOpensOrExports(module, pn, other, open, syncVM);
        } catch (Throwable e) {
            DarkMatterLog.error("Couldn't add new {}. Expect errors", open ? "opens" : "exports");
        }
    }

    @Deprecated(forRemoval = true)
    public static Field tryRemoveFinal(Field field) {
        try {
            MiscReflectionInternals.tryRemoveFinal(field);
        } catch (Throwable e) {
            DarkMatterLog.error("Couldn't remove final from field " + field.getName(), e);
        }
        return field;
    }

    //https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass
    @Deprecated(forRemoval = true)
    public static int getOverrideOffset() {
        return UnsafeInternals.getOverrideOffset();
    }

    @Deprecated(forRemoval = true)
    public static @NotNull MethodHandles.Lookup mockLookupClass(Class<?> clazz) {
        try {
            return MiscReflectionInternals.lookupIn(clazz);
        } catch (Throwable e) {
            throw new RuntimeException("Couldn't mock lookup class", e);
        }
    }

    @Deprecated(forRemoval = true)
    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) {
        try {
            return MiscReflectionInternals.accessRestrictedClass(name, loader);
        } catch (Throwable e) {
            throw new RuntimeException("Couldn't access restricted class", e);
        }
    }

    @Deprecated(forRemoval = true)
    public static Class<?> accessRestrictedClass(String name) {
        try {
            return MiscReflectionInternals.accessRestrictedClass(name, null);
        } catch (Throwable e) {
            throw new RuntimeException("Couldn't access restricted class", e);
        }
    }

    @Deprecated(forRemoval = true)
    public static Field getField(Class<?> clazz, String name) {
        return ReflectionInternals.getField(clazz, name, false);
    }

    @Deprecated(forRemoval = true)
    public static Field getField(Class<?> clazz, String name, boolean accessible) {
        return ReflectionInternals.getField(clazz, name, accessible);
    }

    @Deprecated
    public static Object getField(Field field, Object o) {
        return ReflectionInternals.getField(field, o);
    }

    @Deprecated
    public static void setField(Field field, Object o, Object value) {
        ReflectionInternals.setField(field, o, value);
    }
}
