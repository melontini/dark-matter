package me.melontini.dark_matter.impl.danger.instrumentation;

import me.melontini.dark_matter.api.base.util.MakeSure;
import net.bytebuddy.agent.Installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public final class AgentProvider {

    static File createJarFile() throws IOException {
        String cls = Installer.class.getName().replace('.', '/') + ".class";
        try (var is = Installer.class.getResourceAsStream('/' + cls)) {
            MakeSure.notNull(is, "Cannot locate class file for Byte Buddy installer");

            File jar = File.createTempFile("dark_matter_agent", ".jar");
            jar.deleteOnExit();
            Manifest manifest = ManifestBuilder.create()
                    .put(Attributes.Name.MANIFEST_VERSION, "1.0")
                    .put("Launcher-Agent-Class", Installer.class.getName())
                    .put("Agent-Class", Installer.class.getName())
                    .put("Can-Redefine-Classes", Boolean.TRUE.toString())
                    .put("Can-Retransform-Classes", Boolean.TRUE.toString())
                    .put("Can-Set-Native-Method-Prefix", Boolean.TRUE.toString())
                    .build();
            try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jar), manifest)) {
                jos.putNextEntry(new JarEntry(cls));
                jos.write(is.readAllBytes());
                jos.closeEntry();
            }
            return jar;
        }
    }

    private static class ManifestBuilder {
        public static ManifestBuilder create() {
            return new ManifestBuilder();
        }

        private final Manifest manifest = new Manifest();

        public ManifestBuilder put(String name, String value) {
            manifest.getMainAttributes().put(new Attributes.Name(name), value);
            return this;
        }

        public ManifestBuilder put(Object name, Object value) {
            manifest.getMainAttributes().put(name, value);
            return this;
        }

        public Manifest build() {
            return this.manifest;
        }
    }
}
