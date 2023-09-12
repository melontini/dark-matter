package me.melontini.dark_matter.impl.minecraft.mixin.debug;

import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.dark_matter.impl.minecraft.debug.ValueTrackerImpl;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MinecraftClientRenderMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/ToastManager;draw(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER), method = "render")
    private void dark_matter$renderValueTrack(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Local DrawContext context) {
        ValueTrackerImpl.Renderer.render(context);
    }
}
