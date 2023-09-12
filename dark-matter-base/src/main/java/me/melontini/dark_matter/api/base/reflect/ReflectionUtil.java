package me.melontini.dark_matter.api.base.reflect;

import me.melontini.dark_matter.impl.base.reflect.ReflectionInternals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass">source</a>
 */
@SuppressWarnings("unused")
public class ReflectionUtil {
    private ReflectionUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to find a constructor for the given class that matches the given array of arguments.
     *
     * @param clazz the class for which to find a constructor
     * @param args  the array of arguments that the constructor must be able to accept
     * @return a constructor for the given class that matches the given array of arguments, or null if no such constructor is found
     */
    public static @Nullable <T> Constructor<T> findConstructor(Class<T> clazz, Object... args) {
        return ReflectionInternals.findConstructor(clazz, Arrays.stream(args).toList());
    }

    /**
     * Attempts to find a constructor for the given class that matches the given list of arguments.
     *
     * @param clazz the class for which to find a constructor
     * @param args  the list of arguments that the constructor must be able to accept
     * @return a constructor for the given class that matches the given list of arguments, or null if no such constructor is found
     */
    public static @Nullable <T> Constructor<T> findConstructor(@NotNull Class<T> clazz, List<Object> args) {
        return ReflectionInternals.findConstructor(clazz, args);
    }

    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, Object... args) {
        return ReflectionInternals.findMethod(clazz, name, Arrays.stream(args).toList());
    }

    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, List<Object> args) {
        return ReflectionInternals.findMethod(clazz, name, args);
    }

    public static @Nullable <T> Field findField(@NotNull Class<T> clazz, String name) {
        return ReflectionInternals.findField(clazz, name);
    }

    /**
     * Attempts to set a constructor as accessible.
     *
     * <p>
     * This method uses the vanilla `Constructor.setAccessible(true)` method, but falls back to using `Unsafe.putBoolean(i, true)` in case reflection fails.
     * </p>
     *
     * @param constructor the constructor to set as accessible
     */
    public static <T> Constructor<T> setAccessible(Constructor<T> constructor) {
        return ReflectionInternals.setAccessible(constructor, true);
    }

    /**
     * Attempts to set a method as accessible.
     *
     * <p>
     * This method uses the vanilla `Method.setAccessible(true)` method, but falls back to using `Unsafe.putBoolean(i, true)` in case reflection fails.
     * </p>
     *
     * @param method the method to set as accessible
     */
    public static Method setAccessible(Method method) {
        return ReflectionInternals.setAccessible(method, true);
    }

    /**
     * Attempts to set a field as accessible.
     *
     * <p>
     * This method uses the vanilla `Field.setAccessible(true)` method, but falls back to using `Unsafe.putBoolean(i, true)` in case reflection fails.
     * </p>
     *
     * @param field the field to set as accessible
     */
    public static Field setAccessible(Field field) {
        return ReflectionInternals.setAccessible(field, true);
    }

    public static Field tryRemoveFinal(Field field) {
        return ReflectionInternals.tryRemoveFinal(field);
    }

    public static void addOpensOrExports(Module module, String pn, Module other, boolean open, boolean syncVM) {
        ReflectionInternals.addOpensOrExports(module, pn, other, open, syncVM);
    }

    //https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass
    public static int getOverrideOffset() {
        return ReflectionInternals.getOverrideOffset();
    }

    /**
     * Creates a mock {@link MethodHandles.Lookup} for the given class.
     *
     * @param clazz the class for which to create a mock {@link MethodHandles.Lookup}
     * @return a mock {@link MethodHandles.Lookup} for the given class
     * @throws RuntimeException if an error occurs while creating the mock lookup class
     */
    public static @NotNull MethodHandles.Lookup mockLookupClass(Class<?> clazz) {
        return ReflectionInternals.mockLookupClass(clazz);
    }

    /**
     * Attempts to access a restricted class with the given name.
     *
     * @param name the name of the class to access
     * @return the Class object for the class with the given name
     * @throws RuntimeException if the class cannot be accessed or if an error occurs while accessing it
     */
    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) {
        return ReflectionInternals.accessRestrictedClass(name, loader);
    }

    public static Class<?> accessRestrictedClass(String name) {
        return accessRestrictedClass(name, null);
    }

    public static Field getField(Class<?> clazz, String name) {
        return ReflectionInternals.getField(clazz, name, false);
    }

    public static Field getField(Class<?> clazz, String name, boolean accessible) {
        return ReflectionInternals.getField(clazz, name, accessible);
    }

    public static Object getField(Field field, Object o) {
        return ReflectionInternals.getField(field, o);
    }

    public static void setField(Field field, Object o, Object value) {
        ReflectionInternals.setField(field, o, value);
    }
}
