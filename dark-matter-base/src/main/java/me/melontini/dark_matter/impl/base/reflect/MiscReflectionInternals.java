package me.melontini.dark_matter.impl.base.reflect;

import me.melontini.dark_matter.api.base.util.MakeSure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MiscReflectionInternals {

    public static Function<Object, Object> createGetter(Field field, MethodHandles.Lookup lookup) {
        try {
            MethodHandle getter = lookup.unreflectGetter(field);
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "apply",
                    MethodType.methodType(Function.class, MethodHandle.class),
                    MethodType.methodType(Object.class, Object.class),
                    MethodHandles.exactInvoker(getter.type()),
                    MethodType.methodType(getter.type().wrap().returnType(), field.getDeclaringClass()));
            return (Function<Object, Object>) site.getTarget().invoke(getter);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public static Supplier<Object> createStaticGetter(Field field, MethodHandles.Lookup lookup) {
        try {
            MethodHandle getter = lookup.unreflectGetter(field);
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "get",
                    MethodType.methodType(Supplier.class, MethodHandle.class),
                    MethodType.methodType(Object.class),
                    MethodHandles.exactInvoker(getter.type()),
                    MethodType.methodType(getter.type().wrap().returnType()));
            return (Supplier<Object>) site.getTarget().invoke(getter);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static BiConsumer<Object, Object> createSetter(Field field, MethodHandles.Lookup lookup) {
        try {
            MethodHandle setter = lookup.unreflectSetter(field);
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "accept",
                    MethodType.methodType(BiConsumer.class, MethodHandle.class),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    MethodHandles.exactInvoker(setter.type()),
                    MethodType.methodType(void.class, field.getDeclaringClass(), setter.type().wrap().parameterType(1)));
            return (BiConsumer<Object, Object>) site.getTarget().invoke(setter);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Consumer<Object> createStaticSetter(Field field, MethodHandles.Lookup lookup) {
        try {
            MethodHandle setter = lookup.unreflectSetter(field);
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "accept",
                    MethodType.methodType(Consumer.class, MethodHandle.class),
                    MethodType.methodType(void.class, Object.class),
                    MethodHandles.exactInvoker(setter.type()),
                    MethodType.methodType(void.class, setter.type().wrap().parameterType(0)));
            return (Consumer<Object>) site.getTarget().invoke(setter);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> defineClass(ClassLoader loader, @Nullable String name, byte[] bytes, @Nullable ProtectionDomain domain) throws Throwable {
        return (Class<?>) ReflectionInternals.stealTrustedLookup()
                .findVirtual(loader.getClass(), "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class))
                .invokeWithArguments(loader, name, bytes, 0, bytes.length, domain);
    }

    private static VarHandle modifiers;
    private static MethodHandle setFieldAccessor;

    public static Field tryRemoveFinal(Field f) throws Throwable {
        MakeSure.notNull(f, "Tried to remove final from a null field");

        if (Modifier.isFinal(f.getModifiers())) {
            if (modifiers == null) {
                modifiers = ReflectionInternals.stealTrustedLookup().findVarHandle(Field.class, "modifiers", int.class);
            }
            modifiers.set(f, ((int) modifiers.get(f)) & ~Modifier.FINAL);
            if (setFieldAccessor == null) {
                Class<?> cls = Class.forName("jdk.internal.reflect.FieldAccessor");
                setFieldAccessor = ReflectionInternals.stealTrustedLookup().findVirtual(Field.class, "setFieldAccessor", MethodType.methodType(void.class, cls, boolean.class));
            }
            setFieldAccessor.invokeWithArguments(f, null, false);
            setFieldAccessor.invokeWithArguments(f, null, true);
        }
        return f;
    }

    private static MethodHandle addOpensOrExports;

    @Deprecated
    public static void addOpensOrExports(Module module, String pn, Module other, boolean open, boolean syncVM) throws Throwable {
        if (addOpensOrExports == null) {
            MethodHandles.Lookup lookup = ReflectionInternals.stealTrustedLookup();
            addOpensOrExports = lookup.findVirtual(Module.class, "implAddExportsOrOpens", MethodType.methodType(void.class, String.class, Module.class, boolean.class, boolean.class));
        }
        addOpensOrExports.invokeWithArguments(module, pn, other, open, syncVM);
    }

    public static @NotNull MethodHandles.Lookup mockLookupClass(Class<?> clazz) throws ReflectiveOperationException {
        return ReflectionInternals.stealTrustedLookup().in(clazz);
    }

    private static MethodHandle forName0;

    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) throws Throwable {
        if (forName0 == null) {
            forName0 = ReflectionInternals.stealTrustedLookup()
                    .findStatic(Class.class, "forName0", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class));
        }
        return (Class<?>) forName0.invokeWithArguments(null, name, false, loader, Class.class);
    }
}
