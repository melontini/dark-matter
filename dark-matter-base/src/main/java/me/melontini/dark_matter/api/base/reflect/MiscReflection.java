package me.melontini.dark_matter.api.base.reflect;

import me.melontini.dark_matter.impl.base.reflect.MiscReflectionInternals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MiscReflection {

    public static Function<Object, Object> createGetter(Field field, MethodHandles.Lookup lookup) {
        return MiscReflectionInternals.createGetter(field, lookup);
    }

    public static Supplier<Object> createStaticGetter(Field field, MethodHandles.Lookup lookup) {
        return MiscReflectionInternals.createStaticGetter(field, lookup);
    }

    public static BiConsumer<Object, Object> createSetter(Field field, MethodHandles.Lookup lookup) {
        return MiscReflectionInternals.createSetter(field, lookup);
    }

    public static Consumer<Object> createStaticSetter(Field field, MethodHandles.Lookup lookup) {
        return MiscReflectionInternals.createStaticSetter(field, lookup);
    }

    //
    // no
    //

    public static Field tryRemoveFinal(Field field) throws Throwable {
        return MiscReflectionInternals.tryRemoveFinal(field);
    }

    public static @NotNull MethodHandles.Lookup mockLookupClass(Class<?> clazz) throws ReflectiveOperationException {
        return MiscReflectionInternals.mockLookupClass(clazz);
    }

    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) throws Throwable {
        return MiscReflectionInternals.accessRestrictedClass(name, loader);
    }

    public static Class<?> accessRestrictedClass(String name) throws Throwable {
        return MiscReflectionInternals.accessRestrictedClass(name, null);
    }

    public static Class<?> defineClass(ClassLoader loader, String name, byte[] bytes, @Nullable ProtectionDomain domain) throws Throwable {
        return MiscReflectionInternals.defineClass(loader, name, bytes, domain);
    }
}
