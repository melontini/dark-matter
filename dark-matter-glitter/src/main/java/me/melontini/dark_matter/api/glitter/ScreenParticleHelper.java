package me.melontini.dark_matter.api.glitter;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.glitter.particles.AbstractScreenParticle;
import me.melontini.dark_matter.impl.glitter.ScreenParticleInternals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.particle.ParticleEffect;

import java.util.List;
import java.util.function.Supplier;


@UtilityClass
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public final class ScreenParticleHelper {

    public static void addParticle(AbstractScreenParticle particle) {
        ScreenParticleInternals.addParticle(particle);
    }

    public static void addParticle(Supplier<AbstractScreenParticle> particle) {
        ScreenParticleInternals.addParticle(particle);
    }

    public static void addParticle(ParticleEffect parameters, double x, double y, double velX, double velY) {
        ScreenParticleInternals.addParticle(parameters, x, y, velX, velY);
    }

    public static void addParticle(ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        ScreenParticleInternals.addParticle(parameters, x, y, velX, velY, velZ);
    }

    public static void addParticles(AbstractScreenParticle... particle) {
        ScreenParticleInternals.addParticles(particle);
    }

    public static void addParticles(List<AbstractScreenParticle> particle) {
        ScreenParticleInternals.addParticles(particle);
    }

    public static void addParticles(Supplier<AbstractScreenParticle> particle, int count) {
        ScreenParticleInternals.addParticles(particle, count);
    }

    public static void addParticles(ParticleEffect parameters, double x, double y, double deltaX, double deltaY, double speed, int count) {
        ScreenParticleInternals.addParticles(parameters, x, y, deltaX, deltaY, speed, count);
    }

    /////////////////////////////

    public static void addScreenParticle(AbstractScreenParticle particle) {
        ScreenParticleInternals.addScreenParticle(particle);
    }

    public static void addScreenParticle(Supplier<AbstractScreenParticle> particle) {
        ScreenParticleInternals.addScreenParticle(particle);
    }

    public static void addScreenParticle(ParticleEffect parameters, double x, double y, double velX, double velY) {
        ScreenParticleInternals.addScreenParticle(parameters, x, y, velX, velY);
    }

    public static void addScreenParticle(ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        ScreenParticleInternals.addScreenParticle(parameters, x, y, velX, velY, velZ);
    }

    public static void addScreenParticles(AbstractScreenParticle... particles) {
        ScreenParticleInternals.addScreenParticles(particles);
    }

    public static void addScreenParticles(List<AbstractScreenParticle> particles) {
        ScreenParticleInternals.addScreenParticles(particles);
    }

    public static void addScreenParticles(Supplier<AbstractScreenParticle> particle, int count) {
        ScreenParticleInternals.addScreenParticles(particle, count);
    }

    public static void addScreenParticles(ParticleEffect parameters, double x, double y, double deltaX, double deltaY, double speed, int count) {
        ScreenParticleInternals.addScreenParticles(parameters, x, y, deltaX, deltaY, speed, count);
    }

    /////////////////////////////

    public static void addScreenParticle(Screen screen, AbstractScreenParticle particle) {
        ScreenParticleInternals.addScreenParticle(screen, particle);
    }

    public static void addScreenParticle(Screen screen, Supplier<AbstractScreenParticle> particle) {
        ScreenParticleInternals.addScreenParticle(screen, particle);
    }

    public static void addScreenParticle(Screen screen, ParticleEffect parameters, double x, double y, double velX, double velY) {
        ScreenParticleInternals.addScreenParticle(screen, parameters, x, y, velX, velY);
    }

    public static void addScreenParticle(Screen screen, ParticleEffect parameters, double x, double y, double velX, double velY, double velZ) {
        ScreenParticleInternals.addScreenParticle(screen, parameters, x, y, velX, velY, velZ);
    }

    public static void addScreenParticles(Screen screen, AbstractScreenParticle... particles) {
        ScreenParticleInternals.addScreenParticles(screen, particles);
    }

    public static void addScreenParticles(Screen screen, List<AbstractScreenParticle> particles) {
        ScreenParticleInternals.addScreenParticles(screen, particles);
    }

    public static void addScreenParticles(Screen screen, Supplier<AbstractScreenParticle> particle, int count) {
        ScreenParticleInternals.addScreenParticles(screen, particle, count);
    }

    public static void addScreenParticles(Screen screen, ParticleEffect parameters, double x, double y, double deltaX, double deltaY, double speed, int count) {
        ScreenParticleInternals.addScreenParticles(screen, parameters, x, y, deltaX, deltaY, speed, count);
    }
}
