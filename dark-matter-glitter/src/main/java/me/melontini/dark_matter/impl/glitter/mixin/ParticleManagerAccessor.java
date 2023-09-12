package me.melontini.dark_matter.impl.glitter.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {

    @Invoker("createParticle")
    <T extends ParticleEffect> Particle dark_matter$createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
}
