package me.melontini.dark_matter.api.danger.instrumentation;

import me.melontini.dark_matter.impl.danger.instrumentation.InstrumentationInternals;
import org.objectweb.asm.tree.ClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.*;

public class InstrumentationAccess {

    private InstrumentationAccess() {
        throw new UnsupportedOperationException();
    }

    public static void retransform(AsmTransformer transformer, String... cls) throws TransformationException {
        InstrumentationInternals.retransform(transformer, false, cls);
    }

    public static void retransform(AsmTransformer transformer, boolean export, String... cls) throws TransformationException {
        InstrumentationInternals.retransform(transformer, export, cls);
    }

    public static void retransform(AsmTransformer transformer, Class<?>... cls) throws TransformationException {
        InstrumentationInternals.retransform(transformer, false, cls);
    }

    public static void retransform(AsmTransformer transformer, boolean export, Class<?>... cls) throws TransformationException {
        InstrumentationInternals.retransform(transformer, export, cls);
    }

    public static boolean canInstrument() {
        return InstrumentationInternals.canInstrument();
    }

    public static Instrumentation getInstrumentation() {
        return InstrumentationInternals.getInstrumentation();
    }

    public static Instrumentation get() {
        return InstrumentationInternals.getInstrumentation();
    }

    public static Optional<Instrumentation> getOrEmpty() {
        return Optional.ofNullable(InstrumentationInternals.getInstrumentation());
    }

    public static void addReads(Module module, Module... extraReads) {
        getOrEmpty().ifPresent(instrumentation -> instrumentation.redefineModule(module, new HashSet<>(List.of(extraReads)), Collections.emptyMap(), Collections.emptyMap(), Collections.emptySet(), Collections.emptyMap()));
    }

    public static void addReads(Module module, Set<Module> extraReads) {
        getOrEmpty().ifPresent(instrumentation -> instrumentation.redefineModule(module, extraReads, Collections.emptyMap(), Collections.emptyMap(), Collections.emptySet(), Collections.emptyMap()));
    }

    public static void addExports(Module module, Map<String, Set<Module>> exports) {
        getOrEmpty().ifPresent(instrumentation -> instrumentation.redefineModule(module, Collections.emptySet(), exports, Collections.emptyMap(), Collections.emptySet(), Collections.emptyMap()));
    }

    public static void addOpens(Module module, Map<String, Set<Module>> opens) {
        getOrEmpty().ifPresent(instrumentation -> instrumentation.redefineModule(module, Collections.emptySet(), Collections.emptyMap(), opens, Collections.emptySet(), Collections.emptyMap()));
    }

    public static void addUses(Module module, Class<?>... uses) {
        getOrEmpty().ifPresent(instrumentation -> instrumentation.redefineModule(module, Collections.emptySet(), Collections.emptyMap(), Collections.emptyMap(), new HashSet<>(List.of(uses)), Collections.emptyMap()));
    }

    public static void addUses(Module module, Set<Class<?>> uses) {
        getOrEmpty().ifPresent(instrumentation -> instrumentation.redefineModule(module, Collections.emptySet(), Collections.emptyMap(), Collections.emptyMap(), uses, Collections.emptyMap()));
    }

    public static void addProvides(Module module, Map<Class<?>, List<Class<?>>> provides) {
        getOrEmpty().ifPresent(instrumentation -> instrumentation.redefineModule(module, Collections.emptySet(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptySet(), provides));
    }

    public static long getObjectSize(Object object) {
        return get().getObjectSize(object);
    }

    public static long getObjectsSize(Object... objects) {
        long total = 0;
        for (Object object : objects) {
            total += get().getObjectSize(object);
        }
        return total;
    }

    @FunctionalInterface
    public interface AbstractFileTransformer extends ClassFileTransformer {
        @Override
        byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer);
    }

    @FunctionalInterface
    public interface AsmTransformer {
        ClassNode transform(ClassNode node);
    }
}
