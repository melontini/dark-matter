/*
 This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 If a copy of the MPL was not distributed with this file,
 You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.melontini.dark_matter.impl.danger.instrumentation;

import lombok.Getter;
import me.melontini.dark_matter.api.base.reflect.MiscReflection;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.classes.ThrowableStorage;
import me.melontini.dark_matter.api.danger.instrumentation.InstrumentationAccess;
import me.melontini.dark_matter.api.danger.instrumentation.TransformationException;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.Installer;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author //<a href="https://github.com/Devan-Kerman/GrossFabricHacks/blob/master/src/main/java/net/devtech/grossfabrichacks/instrumentation/InstrumentationApi.java">Devan-Kerman/GrossFabricHacks</a>
 */
@ApiStatus.Internal
@SuppressWarnings("unused")
public class InstrumentationInternals {
    private InstrumentationInternals() {
        throw new UnsupportedOperationException();
    }

    public static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
    public static final Path EXPORT_DIR = GAME_DIR.resolve(".dark-matter/class");
    public static final Path AGENT_DIR = GAME_DIR.resolve(".dark-matter/agent");
    @Getter
    private static Instrumentation instrumentation;
    private static boolean canInstrument = false;

    public static void retransform(InstrumentationAccess.AsmTransformer transformer, boolean export, String... cls) throws TransformationException {
        Class<?>[] classes = Arrays.stream(cls).map(s -> {
            try {
                return Class.forName(s);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Couldn't access %s class!".formatted(s), e);
            }
        }).toArray(Class[]::new);
        retransform(transformer, export, classes);
    }

    public static void retransform(InstrumentationAccess.AsmTransformer transformer, boolean export, Class<?>... cls) throws TransformationException {
        HashSet<Class<?>> classes = Arrays.stream(cls).collect(Collectors.toCollection(HashSet::new));
        ThrowableStorage<Throwable> throwable = ThrowableStorage.of();
        InstrumentationAccess.AbstractFileTransformer fileTransformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (classes.contains(classBeingRedefined)) {
                try {
                    ClassReader reader = new ClassReader(classfileBuffer);
                    ClassNode node = new ClassNode();
                    reader.accept(node, ClassReader.EXPAND_FRAMES);
                    node = transformer.transform(node);
                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                    node.accept(writer);
                    byte[] clsFile = writer.toByteArray();
                    if (export) {
                        try {
                            Path path = EXPORT_DIR.resolve(className.replace(".", "/") + ".class");
                            Files.createDirectories(path.getParent());
                            Files.write(path, clsFile);
                        } catch (IOException e) {
                            DarkMatterLog.error(String.format("Couldn't export %s", className), e);
                        }
                    }
                    return clsFile;
                } catch (Throwable t) {
                    throwable.set(t);
                    return classfileBuffer;
                }
            }

            return classfileBuffer;
        };

        try {
            instrumentation.addTransformer(fileTransformer, true);
            instrumentation.retransformClasses(cls);
            instrumentation.removeTransformer(fileTransformer);
            throwable.tryThrow();
        } catch (Throwable t) {
            throw new TransformationException("Failed to retransform classes %s".formatted(Arrays.toString(cls)), t);
        }
    }

    public static boolean canInstrument() {
        return canInstrument;
    }

    static {
        try {
            if (Files.exists(EXPORT_DIR)) {
                Files.walkFileTree(EXPORT_DIR, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (!dir.equals(EXPORT_DIR)) {
                            Files.delete(dir);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (Exception e) {
            DarkMatterLog.error(String.format("Couldn't clean %s", EXPORT_DIR), e);
        }

        bootstrap();
    }

    public static void bootstrap() {
        if (canInstrument()) return;

        try {
            Files.deleteIfExists(AGENT_DIR.resolve("dark_matter_instrumentation_agent.jar"));
        } catch (IOException ignored) {
            // rip
        }
        try {
            try {
                instrumentation = unsafeAttach();
            } catch (Throwable t) {
                DarkMatterLog.info("Failed to attach using InstrumentationImpl#loadAgent, trying default.", t);
                instrumentation = ByteBuddyAgent.install();
            }

            canInstrument = true;
            DarkMatterLog.info("Successfully attached instrumentation agent.");
        } catch (final Throwable throwable) {
            DarkMatterLog.error("An error occurred during an attempt to attach an instrumentation agent.", throwable);
        }
    }

    private static Instrumentation unsafeAttach() throws Throwable {
        File self = AgentProvider.createJarFile();

        try (var is = Installer.class.getClassLoader().getResourceAsStream(Installer.class.getName().replace(".", "/") + ".class")) {
            MiscReflection.defineClass(ClassLoader.getSystemClassLoader(), Installer.class.getName(), MakeSure.notNull(is).readAllBytes(), Installer.class.getProtectionDomain());
        } catch (Throwable ignored) {}


        AtomicReference<Throwable> t = new AtomicReference<>();
        ModuleLayer.boot().findModule("java.instrument").map(module -> {
            try {
                Class<?> cls = Class.forName("sun.instrument.InstrumentationImpl");
                MethodHandles.Lookup lookup = MiscReflection.lookupIn(cls);
                lookup.findStatic(cls, "loadAgent", MethodType.methodType(void.class, String.class))
                        .invokeWithArguments(self.toString());
            } catch (Throwable e) {
                Throwable throwable = e;
                if (throwable instanceof InvocationTargetException) throwable = throwable.getCause();
                t.set(throwable);
            }
            return self;
        }).orElseThrow(() -> new IllegalStateException("'java.instrument' module is not available!"));
        if (t.get() != null) throw t.get();

        return ByteBuddyAgent.getInstrumentation();
    }
}
