package me.melontini.dark_matter.impl.base.reflect;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

@UtilityClass
public class MiscReflectionInternals {

    private static final Lazy<MethodHandle> defineClass = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class)));

    public static Class<?> defineClass(ClassLoader loader, @Nullable String name, byte[] bytes, @Nullable ProtectionDomain domain) throws Throwable {
        return (Class<?>) defineClass.getExc().invokeWithArguments(loader, name, bytes, 0, bytes.length, domain);
    }

    private static final Lazy<VarHandle> modifiers = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findVarHandle(Field.class, "modifiers", int.class));
    private static final Lazy<VarHandle> fieldAccessor = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findVarHandle(Field.class, "fieldAccessor", Class.forName("jdk.internal.reflect.FieldAccessor")));
    private static final Lazy<VarHandle> overrideFieldAccessor = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findVarHandle(Field.class, "overrideFieldAccessor", Class.forName("jdk.internal.reflect.FieldAccessor")));

    public static Field tryRemoveFinal(Field f) throws Throwable {
        MakeSure.notNull(f, "Tried to remove final from a null field");

        if (Modifier.isFinal(f.getModifiers())) {
            modifiers.getExc().set(f, ((int) modifiers.get().get(f)) & ~Modifier.FINAL);
            fieldAccessor.getExc().set(f, null);
            overrideFieldAccessor.getExc().set(f, null);
        }
        return f;
    }

    private static final Lazy<MethodHandle> addOpensOrExports = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findVirtual(Module.class, "implAddExportsOrOpens", MethodType.methodType(void.class, String.class, Module.class, boolean.class, boolean.class)));
    private static final Lazy<MethodHandle> addUses = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findVirtual(Module.class, "implAddUses", MethodType.methodType(void.class, Class.class)));
    private static final Lazy<MethodHandle> addReads = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findVirtual(Module.class, "implAddReads", MethodType.methodType(void.class, Module.class, boolean.class)));

    public static void addOpensOrExports(Module module, String pn, Module other, boolean open, boolean syncVM) throws Throwable {
        addOpensOrExports.getExc().invokeWithArguments(module, pn, other, open, syncVM);
    }
    public static void addUses(Module module, Class<?> clazz) throws Throwable {
        addUses.getExc().invokeWithArguments(module, clazz);
    }
    public static void addReads(Module module, Module other, boolean syncVM) throws Throwable {
        addReads.getExc().invokeWithArguments(module, other, syncVM);
    }

    public static @NotNull MethodHandles.Lookup lookupIn(Class<?> clazz) throws Exception {
        return ReflectionInternals.trustedLookup().in(clazz);
    }

    private static final Lazy<MethodHandle> forName0 = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findStatic(Class.class, "forName0", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class)));

    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) throws Throwable {
        return (Class<?>) forName0.getExc().invokeWithArguments(null, name, false, loader, Class.class);
    }

    private static final Lazy<MethodHandle> makeFieldHandle = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findStatic(Class.forName("java.lang.invoke.VarHandles"), "makeFieldHandle", MethodType.methodType(VarHandle.class, Class.forName("java.lang.invoke.MemberName"), Class.class, Class.class, boolean.class)));
    private static final Lazy<MethodHandle> memberNameCtx = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findConstructor(Class.forName("java.lang.invoke.MemberName"), MethodType.methodType(void.class, Field.class, boolean.class)));

    public static VarHandle unreflectVarHandle(Field f) throws Throwable {
        Object m = memberNameCtx.getExc().invoke(f, false);
        return (VarHandle) makeFieldHandle.getExc().invoke(m, f.getDeclaringClass(), f.getType(), true);
    }

    private static final Lazy<MethodHandle> resolveOrFail = Lazy.of(() -> () -> ReflectionInternals.trustedLookup().findVirtual(MethodHandles.Lookup.class, "resolveOrFail", MethodType.methodType(Class.forName("java.lang.invoke.MemberName"), byte.class, Class.class, String.class, Class.class)));

    public static VarHandle findVarHandle(Class<?> cls, String name, Class<?> type) throws Throwable {
        Object m = resolveOrFail.getExc().invokeWithArguments(ReflectionInternals.trustedLookup(), (byte) 1, cls, name, type);
        return (VarHandle) makeFieldHandle.getExc().invoke(m, cls, type, true);
    }

    public static VarHandle findStaticVarHandle(Class<?> cls, String name, Class<?> type) throws Throwable {
        Object m = resolveOrFail.getExc().invokeWithArguments(ReflectionInternals.trustedLookup(), (byte) 2, cls, name, type);
        return (VarHandle) makeFieldHandle.getExc().invoke(m, cls, type, true);
    }
}
