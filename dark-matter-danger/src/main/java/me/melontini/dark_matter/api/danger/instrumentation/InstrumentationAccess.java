package me.melontini.dark_matter.api.danger.instrumentation;

import me.melontini.dark_matter.impl.danger.instrumentation.InstrumentationInternals;
import org.objectweb.asm.tree.ClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Optional;

public class InstrumentationAccess {

    private InstrumentationAccess() {
        throw new UnsupportedOperationException();
    }

    public static void retransform(AsmTransformer transformer, String... cls) {
        InstrumentationInternals.retransform(transformer, false, cls);
    }

    public static void retransform(AsmTransformer transformer, boolean export, String... cls) {
        InstrumentationInternals.retransform(transformer, export, cls);
    }

    public static void retransform(AsmTransformer transformer, Class<?>... cls) {
        InstrumentationInternals.retransform(transformer, false, cls);
    }

    public static void retransform(AsmTransformer transformer, boolean export, Class<?>... cls) {
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
