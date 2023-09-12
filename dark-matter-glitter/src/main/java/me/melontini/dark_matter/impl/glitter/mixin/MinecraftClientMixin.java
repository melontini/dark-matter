package me.melontini.dark_matter.impl.glitter.mixin;

import me.melontini.dark_matter.impl.glitter.ScreenParticleInternals;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;tick(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/hit/HitResult;)V", shift = At.Shift.AFTER))
    private void dark_matter$tickParticles(CallbackInfo ci) {
        ScreenParticleInternals.tickParticles();
    }

    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;tick()V"))
    private void dark_matter$tickScreen(CallbackInfo ci) {
        ScreenParticleInternals.tickParticles();
    }
}
