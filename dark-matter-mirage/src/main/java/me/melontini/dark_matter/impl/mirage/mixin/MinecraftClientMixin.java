package me.melontini.dark_matter.impl.mirage.mixin;

import me.melontini.dark_matter.impl.mirage.FakeWorld;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "method_29338", at = @At("TAIL"), require = 0)
    private void dark_matter$init(CallbackInfo ci) {
        MinecraftClient.getInstance().send(FakeWorld::init);
    }
}
