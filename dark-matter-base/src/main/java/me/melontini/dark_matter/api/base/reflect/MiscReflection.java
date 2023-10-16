package me.melontini.dark_matter.api.base.reflect;

import me.melontini.dark_matter.impl.base.reflect.MiscReflectionInternals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;

public class MiscReflection {

    //
    // no
    //

    public static Field tryRemoveFinal(Field field) throws Throwable {
        return MiscReflectionInternals.tryRemoveFinal(field);
    }

    public static @NotNull MethodHandles.Lookup lookupIn(Class<?> clazz) throws Exception {
        return MiscReflectionInternals.lookupIn(clazz);
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

    /**
     * Creates a VarHandle which has the ability to write both final and static final addresses
     */
    public static VarHandle unreflectVarHandle(Field f) throws Throwable {
        return MiscReflectionInternals.unreflectVarHandle(f);
    }

    public static VarHandle findVarHandle(Class<?> cls, String name, Class<?> type) throws Throwable {
        return MiscReflectionInternals.findVarHandle(cls, name, type);
    }

    public static VarHandle findStaticVarHandle(Class<?> cls, String name, Class<?> type) throws Throwable {
        return MiscReflectionInternals.findStaticVarHandle(cls, name, type);
    }
}
