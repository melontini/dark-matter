package me.melontini.dark_matter.api.base.reflect;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.base.reflect.ReflectionInternals;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@UtilityClass
@SuppressWarnings("unused")
public class Reflect {

    //
    // find members
    //

    public static <T> Optional<Constructor<T>> findConstructor(@NotNull Class<T> clazz, Object... args) {
        return Optional.ofNullable(ReflectionInternals.findConstructor(clazz, Arrays.stream(args).toList()));
    }

    public static <T> Optional<Constructor<T>> findConstructor(@NotNull Class<T> clazz, List<Object> args) {
        return Optional.ofNullable(ReflectionInternals.findConstructor(clazz, args));
    }

    public static Optional<Method> findMethod(@NotNull Class<?> clazz, String name, Object... args) {
        return Optional.ofNullable(ReflectionInternals.findMethod(clazz, name, Arrays.stream(args).toList()));
    }

    public static Optional<Method> findMethod(@NotNull Class<?> clazz, String name, List<Object> args) {
        return Optional.ofNullable(ReflectionInternals.findMethod(clazz, name, args));
    }

    public static Optional<Field> findField(@NotNull Class<?> clazz, String name) {
        return Optional.ofNullable(ReflectionInternals.findField(clazz, name));
    }

    //
    // set accessible
    //

    public static <T extends AccessibleObject> T setAccessible(T member) {
        return ReflectionInternals.setAccessible(member, true);
    }

    public static <T extends AccessibleObject> T setAccessible(T member, boolean set) {
        return ReflectionInternals.setAccessible(member, set);
    }
}
