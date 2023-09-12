package me.melontini.dark_matter.impl.minecraft.mixin.debug;

import me.melontini.dark_matter.impl.minecraft.debug.ValueTrackerImpl;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientTickMixin {

    @Inject(at = @At("TAIL"), method = "tick")
    private void dark_matter$tickValueTrack(CallbackInfo ci) {
        ValueTrackerImpl.tick();
    }

    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;tick()V"))
    private void dark_matter$tickValueTrackIntegratedServer(CallbackInfo ci) {
        ValueTrackerImpl.tick();
    }
}
