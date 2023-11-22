package me.melontini.dark_matter.impl.glitter;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.glitter.particles.AbstractScreenParticle;
import me.melontini.dark_matter.impl.glitter.particles.VanillaParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Supplier;

@UtilityClass
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ScreenParticleInternals {

    private static final Set<AbstractScreenParticle> SCREEN_PARTICLES = new LinkedHashSet<>();
    private static final Set<AbstractScreenParticle> SCREEN_PARTICLES_REMOVAL = new HashSet<>();
    public static final Random RANDOM = new Random();

    public static void addParticle(AbstractScreenParticle particle) {
        SCREEN_PARTICLES.add(particle);
    }

    public static void addParticle(Supplier<AbstractScreenParticle> particle) {
        SCREEN_PARTICLES.add(particle.get());
    }

    public static void addParticle(ParticleEffect parameters, double x, double y, double velX, double velY) {
        MakeSure.notNull(parameters, "Tried to add a screen particle with null parameters");
        SCREEN_PARTICLES.add(new VanillaParticle(parameters, x, y, velX, velY));
    }

    public static void addParticle(ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        MakeSure.notNull(parameters, "Tried to add a screen particle with null parameters");
        SCREEN_PARTICLES.add(new VanillaParticle(parameters, x, y, velX, velY, velZ));
    }

    public static void addParticles(AbstractScreenParticle... particle) {
        SCREEN_PARTICLES.addAll(List.of(particle));
    }

    public static void addParticles(List<AbstractScreenParticle> particle) {
        SCREEN_PARTICLES.addAll(particle);
    }

    public static void addParticles(Supplier<AbstractScreenParticle> particle, int count) {
        for (int i = 0; i < count; i++) {
            SCREEN_PARTICLES.add(particle.get());
        }
    }

    public static void addParticles(ParticleEffect parameters, double x, double y, double deltaX, double deltaY, double speed, int count) {
        MakeSure.notNull(parameters, "Tried to add screen particles with null parameters");
        MakeSure.isTrue(count >= 0, "Count can't be below 0!");

        for (int i = 0; i < count; i++) {
            double offsetX = RANDOM.nextGaussian() * deltaX;
            double offsetY = RANDOM.nextGaussian() * deltaY;
            double velX = RANDOM.nextGaussian() * speed;
            double velY = RANDOM.nextGaussian() * speed;

            SCREEN_PARTICLES.add(new VanillaParticle(parameters, x + offsetX, y + offsetY, velX, velY));
        }
    }

    /////////////////////////////

    public static void addScreenParticle(AbstractScreenParticle particle) {
        particle.bindToScreen(MinecraftClient.getInstance().currentScreen);
        SCREEN_PARTICLES.add(particle);
    }

    public static void addScreenParticle(Supplier<AbstractScreenParticle> particle) {
        AbstractScreenParticle particle1 = particle.get();
        particle1.bindToScreen(MinecraftClient.getInstance().currentScreen);
        SCREEN_PARTICLES.add(particle1);
    }

    public static void addScreenParticle(ParticleEffect parameters, double x, double y, double velX, double velY) {
        MakeSure.notNull(parameters, "Tried to add a screen particle with null parameters");
        VanillaParticle particle = new VanillaParticle(parameters, x, y, velX, velY);
        particle.bindToScreen(MinecraftClient.getInstance().currentScreen);
        SCREEN_PARTICLES.add(particle);
    }

    public static void addScreenParticle(ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        MakeSure.notNull(parameters, "Tried to add a screen particle with null parameters");
        VanillaParticle particle = new VanillaParticle(parameters, x, y, velX, velY, velZ);
        particle.bindToScreen(MinecraftClient.getInstance().currentScreen);
        SCREEN_PARTICLES.add(particle);
    }

    public static void addScreenParticles(AbstractScreenParticle... particle) {
        for (AbstractScreenParticle abstractScreenParticle : particle) {
            abstractScreenParticle.bindToScreen(MinecraftClient.getInstance().currentScreen);
        }
        SCREEN_PARTICLES.addAll(List.of(particle));
    }

    public static void addScreenParticles(List<AbstractScreenParticle> particle) {
        for (AbstractScreenParticle abstractScreenParticle : particle) {
            abstractScreenParticle.bindToScreen(MinecraftClient.getInstance().currentScreen);
        }
        SCREEN_PARTICLES.addAll(particle);
    }

    public static void addScreenParticles(Supplier<AbstractScreenParticle> particle, int count) {
        for (int i = 0; i < count; i++) {
            AbstractScreenParticle particle1 = particle.get();
            particle1.bindToScreen(MinecraftClient.getInstance().currentScreen);
            SCREEN_PARTICLES.add(particle1);
        }
    }

    public static void addScreenParticles(ParticleEffect parameters, double x, double y, double deltaX, double deltaY, double speed, int count) {
        MakeSure.notNull(parameters, "Tried to add screen particles with null parameters");
        MakeSure.isTrue(count >= 0, "Count can't be below 0!");

        for (int i = 0; i < count; i++) {
            double offsetX = RANDOM.nextGaussian() * deltaX;
            double offsetY = RANDOM.nextGaussian() * deltaY;
            double velX = RANDOM.nextGaussian() * speed;
            double velY = RANDOM.nextGaussian() * speed;

            VanillaParticle particle = new VanillaParticle(parameters, x + offsetX, y + offsetY, velX, velY);
            particle.bindToScreen(MinecraftClient.getInstance().currentScreen);
            SCREEN_PARTICLES.add(particle);
        }
    }

    /////////////////////////////

    public static void addScreenParticle(Screen screen, AbstractScreenParticle particle) {
        particle.bindToScreen(screen);
        SCREEN_PARTICLES.add(particle);
    }

    public static void addScreenParticle(Screen screen, Supplier<AbstractScreenParticle> particle) {
        AbstractScreenParticle particle1 = particle.get();
        particle1.bindToScreen(screen);
        SCREEN_PARTICLES.add(particle1);
    }

    public static void addScreenParticle(Screen screen, ParticleEffect parameters, double x, double y, double velX, double velY) {
        MakeSure.notNull(parameters, "Tried to add a screen particle with null parameters");
        VanillaParticle particle = new VanillaParticle(parameters, x, y, velX, velY);
        particle.bindToScreen(screen);
        SCREEN_PARTICLES.add(particle);
    }

    public static void addScreenParticle(Screen screen, ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        MakeSure.notNull(parameters, "Tried to add a screen particle with null parameters");
        VanillaParticle particle = new VanillaParticle(parameters, x, y, velX, velY, velZ);
        particle.bindToScreen(screen);
        SCREEN_PARTICLES.add(particle);
    }

    public static void addScreenParticles(Screen screen, AbstractScreenParticle... particle) {
        for (AbstractScreenParticle abstractScreenParticle : particle) {
            abstractScreenParticle.bindToScreen(screen);
        }
        SCREEN_PARTICLES.addAll(List.of(particle));
    }

    public static void addScreenParticles(Screen screen, List<AbstractScreenParticle> particle) {
        for (AbstractScreenParticle abstractScreenParticle : particle) {
            abstractScreenParticle.bindToScreen(screen);
        }
        SCREEN_PARTICLES.addAll(particle);
    }

    public static void addScreenParticles(Screen screen, Supplier<AbstractScreenParticle> particle, int count) {
        for (int i = 0; i < count; i++) {
            AbstractScreenParticle particle1 = particle.get();
            particle1.bindToScreen(screen);
            SCREEN_PARTICLES.add(particle1);
        }
    }

    public static void addScreenParticles(Screen screen, ParticleEffect parameters, double x, double y, double deltaX, double deltaY, double speed, int count) {
        MakeSure.notNull(parameters, "Tried to add screen particles with null parameters");
        MakeSure.isTrue(count >= 0, "Count can't be below 0!");

        for (int i = 0; i < count; i++) {
            double offsetX = RANDOM.nextGaussian() * deltaX;
            double offsetY = RANDOM.nextGaussian() * deltaY;
            double velX = RANDOM.nextGaussian() * speed;
            double velY = RANDOM.nextGaussian() * speed;

            VanillaParticle particle = new VanillaParticle(parameters, x + offsetX, y + offsetY, velX, velY);
            particle.bindToScreen(screen);
            SCREEN_PARTICLES.add(particle);
        }
    }

    public static void renderParticles(MinecraftClient client, DrawContext context) {
        if (SCREEN_PARTICLES.isEmpty()) return;

        int i = (int) (client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth());
        int j = (int) (client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight());
        for (AbstractScreenParticle particle : SCREEN_PARTICLES) {
            particle.renderInternal(context, i, j, client.getTickDelta());
        }
    }

    public static void tickParticles() {
        if (SCREEN_PARTICLES.isEmpty()) return;

        for (AbstractScreenParticle particle : SCREEN_PARTICLES) {
            particle.tickInternal();
            if (particle.removed) SCREEN_PARTICLES_REMOVAL.add(particle);
        }

        SCREEN_PARTICLES.removeIf(SCREEN_PARTICLES_REMOVAL::contains);
        SCREEN_PARTICLES_REMOVAL.clear();
    }
}
