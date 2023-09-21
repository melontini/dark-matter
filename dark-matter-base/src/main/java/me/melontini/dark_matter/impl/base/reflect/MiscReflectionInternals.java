package me.melontini.dark_matter.impl.base.reflect;

import java.lang.invoke.*;
import java.lang.reflect.Field;
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
}
