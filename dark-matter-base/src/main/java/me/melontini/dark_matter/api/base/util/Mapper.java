package me.melontini.dark_matter.api.base.util;

import me.melontini.dark_matter.impl.base.util.MapperInternals;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodType;

public class Mapper {

    public static String mapClass(String name) {
        return MapperInternals.mapClass(name);
    }

    public static String unmapClass(String name) {
        return MapperInternals.unmapClass(name);
    }

    public static String unmapClass(Class<?> cls) {
        return MapperInternals.unmapClass(cls);
    }

    public static String mapField(String owner, String field, String desc) {
        return MapperInternals.mapField(owner, field, desc);
    }

    public static String mapField(String owner, String field, Class<?> desc) {
        return MapperInternals.mapField(owner, field, unmapDescriptor(Type.getDescriptor(desc)));
    }

    public static String mapField(Class<?> owner, String field, String desc) {
        return MapperInternals.mapField(unmapClass(owner), field, desc);
    }

    public static String mapField(Class<?> owner, String field, Class<?> desc) {
        return MapperInternals.mapField(unmapClass(owner), field, unmapDescriptor(Type.getDescriptor(desc)));
    }

    public static String mapMethod(String owner, String method, String desc) {
        return MapperInternals.mapMethod(owner, method, desc);
    }

    public static String mapMethod(String owner, String method, MethodType desc) {
        return MapperInternals.mapMethod(owner, method, unmapMethodDescriptor(desc));
    }

    public static String mapMethod(Class<?> owner, String method, String desc) {
        return MapperInternals.mapMethod(unmapClass(owner), method, desc);
    }

    public static String mapMethod(Class<?> owner, String method, MethodType desc) {
        return MapperInternals.mapMethod(unmapClass(owner), method, unmapMethodDescriptor(desc));
    }

    public static String mapMethodDescriptor(String descriptor) {
        return mapMethodDescriptor(Type.getMethodType(descriptor));
    }

    public static String mapMethodDescriptor(Type descriptor) {
        return MapperInternals.mapMethodDescriptor(descriptor);
    }

    public static String unmapMethodDescriptor(MethodType type) {
        return unmapMethodDescriptor(type.toMethodDescriptorString());
    }

    public static String unmapMethodDescriptor(String descriptor) {
        return unmapMethodDescriptor(Type.getMethodType(descriptor));
    }

    public static String unmapMethodDescriptor(Type descriptor) {
        return MapperInternals.unmapMethodDescriptor(descriptor);
    }

    public static String mapDescriptor(String arg) {
        return MapperInternals.mapDescriptor(arg);
    }

    public static String unmapDescriptor(String arg) {
        return MapperInternals.unmapDescriptor(arg);
    }
}
