package me.melontini.dark_matter.api.base.reflect;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.base.reflect.MiscReflectionInternals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;

@UtilityClass
public class MiscReflection {

    //
    // no
    //

    public static @NotNull MethodHandles.Lookup lookupIn(Class<?> clazz) throws Exception {
        return MiscReflectionInternals.lookupIn(clazz);
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

    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) throws Throwable {
        return MiscReflectionInternals.accessRestrictedClass(name, loader);
    }

    public static Class<?> accessRestrictedClass(String name) throws Throwable {
        return MiscReflectionInternals.accessRestrictedClass(name, null);
    }

    public static Class<?> defineClass(ClassLoader loader, String name, byte[] bytes, @Nullable ProtectionDomain domain) throws Throwable {
        return MiscReflectionInternals.defineClass(loader, name, bytes, domain);
    }

    public static void addOpensOrExports(Module module, String pn, Module other, boolean open, boolean syncVM) throws Throwable {
        MiscReflectionInternals.addOpensOrExports(module, pn, other, open, syncVM);
    }
    public static void addUses(Module module, Class<?> clazz) throws Throwable {
        MiscReflectionInternals.addUses(module, clazz);
    }
    public static void addReads(Module module, Module other, boolean syncVM) throws Throwable {
        MiscReflectionInternals.addReads(module, other, syncVM);
    }

    public static Field tryRemoveFinal(Field field) throws Throwable {
        return MiscReflectionInternals.tryRemoveFinal(field);
    }
}
