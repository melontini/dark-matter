/*
 This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 If a copy of the MPL was not distributed with this file,
 You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.melontini.dark_matter.impl.danger.instrumentation;

import me.melontini.dark_matter.api.base.reflect.ReflectionUtil;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.danger.instrumentation.InstrumentationAccess;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
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
    private static Instrumentation instrumentation;
    private static boolean canInstrument = false;

    public static void retransform(InstrumentationAccess.AsmTransformer transformer, boolean export, String... cls) {
        try {
            Class<?>[] classes = Arrays.stream(cls).map(s -> {
                try {
                    return Class.forName(s);
                } catch (ClassNotFoundException e) {
                    try {
                        return ReflectionUtil.accessRestrictedClass(s);
                    } catch (Exception e1) {
                        throw new RuntimeException(String.format("Couldn't access %s class!", s), e1);
                    }
                }
            }).toArray(Class[]::new);
            retransform(transformer, export, classes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void retransform(InstrumentationAccess.AsmTransformer transformer, boolean export, Class<?>... cls) {
        try {
            HashSet<Class<?>> classes = Arrays.stream(cls).collect(Collectors.toCollection(HashSet::new));
            InstrumentationAccess.AbstractFileTransformer fileTransformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
                if (classes.contains(classBeingRedefined)) {
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
                }

                return classfileBuffer;
            };

            instrumentation.addTransformer(fileTransformer, true);
            instrumentation.retransformClasses(cls);
            instrumentation.removeTransformer(fileTransformer);
        } catch (Exception e) {
            throw new RuntimeException("Exception while retransforming classes. Have you subscribed to danger in fabric.mod.json?", e);
        }
    }

    public static boolean canInstrument() {
        return canInstrument;
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
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
            try {
                instrumentation = tryAttachDarkAgent();
            } catch (Exception e) {
                DarkMatterLog.info("Failed to attach DM agent, trying the ByteBuddy one.");
                instrumentation = ByteBuddyAgent.install();
            }

            canInstrument = true;
            DarkMatterLog.info("Successfully attached instrumentation agent.");
        } catch (final Throwable throwable) {
            DarkMatterLog.error("An error occurred during an attempt to attach an instrumentation agent.", throwable);
        }
    }

    private static Instrumentation tryAttachDarkAgent() throws Exception {
        final String name = ManagementFactory.getRuntimeMXBean().getName();
        final Path jarPath = AGENT_DIR.resolve("dark_matter_instrumentation_agent.jar");
        final File jar = jarPath.toFile();

        DarkMatterLog.info("Attaching instrumentation agent to VM.");

        if (!Files.exists(jarPath)) {
            createAgentJar(jarPath, jar);
        } else {
            try (InputStream stream = InstrumentationInternals.class.getClassLoader().getResourceAsStream("jar/dark_matter_instrumentation_agent.jar")) {
                if (stream != null) {
                    byte[] bytes = stream.readAllBytes();
                    if (!Arrays.equals(Files.readAllBytes(jarPath), bytes)) {
                        DarkMatterLog.info("Newer jar found, overwriting the old one...");
                        Files.delete(jarPath);
                        createAgentJar(jarPath, jar);
                    }
                } else {
                    throw new NullPointerException("Couldn't find included \"jar/dark_matter_instrumentation_agent.jar\"! Couldn't check jar version! Trying to attach anyway...");
                }
            }
        }
        ByteBuddyAgent.attach(jar, name.substring(0, name.indexOf('@')));

        final Field field = Class.forName("me.melontini.dark_matter.impl.danger.instrumentation.InstrumentationAgent", false, ClassLoader.getSystemClassLoader()).getField("instrumentation");
        field.setAccessible(true);
        return MakeSure.notNull((Instrumentation) field.get(null));
    }

    private static void createAgentJar(Path jarPath, File jar) throws IOException {
        Files.createDirectories(jarPath.getParent());
        try (InputStream stream = InstrumentationInternals.class.getClassLoader().getResourceAsStream("jar/dark_matter_instrumentation_agent.jar")) {
            if (stream != null) {
                try (FileOutputStream outputStream = new FileOutputStream(jar)) {
                    outputStream.write(stream.readAllBytes());
                }
            } else {
                throw new NullPointerException("Couldn't find included \"jar/dark_matter_instrumentation_agent.jar\"!");
            }
        }
    }
}
