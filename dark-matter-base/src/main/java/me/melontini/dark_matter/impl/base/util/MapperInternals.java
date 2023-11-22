package me.melontini.dark_matter.impl.base.util;

import lombok.experimental.UtilityClass;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Type;

@UtilityClass
public class MapperInternals {

    private static final MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();

    static {
        verifyIntermediary();
    }

    private static void verifyIntermediary() {
        for (String namespace : RESOLVER.getNamespaces()) {
            if ("intermediary".equals(namespace)) return;
        }
        throw new IllegalStateException("Intermediary namespace not found");
    }

    public static String mapClass(String name) {
        return RESOLVER.mapClassName("intermediary", name);
    }

    public static String unmapClass(String name) {
        return RESOLVER.unmapClassName("intermediary", name);
    }

    public static String unmapClass(Class<?> cls) {
        return RESOLVER.unmapClassName("intermediary", cls.getName());
    }

    public static String mapField(String owner, String field, String desc) {
        return RESOLVER.mapFieldName("intermediary", owner, field, desc);
    }

    public static String mapMethod(String owner, String method, String desc) {
        return RESOLVER.mapMethodName("intermediary", owner, method, desc);
    }

    public static String mapMethodDescriptor(Type descriptor) {
        StringBuilder mappedDesc = new StringBuilder("(");
        for (Type argumentType : descriptor.getArgumentTypes()) {
            mappedDesc.append(mapDescriptor(argumentType.getDescriptor()));
        }
        mappedDesc.append(")");
        mappedDesc.append(mapDescriptor(descriptor.getReturnType().getDescriptor()));
        return mappedDesc.toString();
    }

    public static String unmapMethodDescriptor(Type descriptor) {
        StringBuilder mappedDesc = new StringBuilder("(");
        for (Type argumentType : descriptor.getArgumentTypes()) {
            mappedDesc.append(unmapDescriptor(argumentType.getDescriptor()));
        }
        mappedDesc.append(")");
        mappedDesc.append(unmapDescriptor(descriptor.getReturnType().getDescriptor()));
        return mappedDesc.toString();
    }

    public static String mapDescriptor(String arg) {
        if (arg.startsWith("L")) arg = "L" + RESOLVER.mapClassName("intermediary", arg.substring(1, arg.length() - 1).replace("/", ".")).replace(".", "/") + ";";
        if (arg.startsWith("[")) arg = "[L" + RESOLVER.mapClassName("intermediary", arg.substring(2, arg.length() - 1).replace("/", ".")).replace(".", "/") + ";";
        return arg;
    }

    public static String unmapDescriptor(String arg) {
        if (arg.startsWith("L")) arg = "L" + RESOLVER.unmapClassName("intermediary", arg.substring(1, arg.length() - 1).replace("/", ".")).replace(".", "/") + ";";
        if (arg.startsWith("[")) arg = "[L" + RESOLVER.unmapClassName("intermediary", arg.substring(2, arg.length() - 1).replace("/", ".")).replace(".", "/") + ";";
        return arg;
    }
}
