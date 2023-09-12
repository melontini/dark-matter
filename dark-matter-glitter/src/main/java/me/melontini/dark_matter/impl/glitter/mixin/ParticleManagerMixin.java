package me.melontini.dark_matter.impl.glitter.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.melontini.dark_matter.impl.glitter.particles.VanillaParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @ModifyExpressionValue(at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/ParticleManager;world:Lnet/minecraft/client/world/ClientWorld;"), method = "createParticle")
    private static ClientWorld dark_matter$modifyWorld(ClientWorld value) {
        if (VanillaParticle.WORLD.get() != null)
            return VanillaParticle.WORLD.get();
        return value;
    }
}
