package me.melontini.dark_matter.impl.analytics.crashes.mixin;

import me.melontini.dark_matter.impl.analytics.crashes.CrashlyticsInternals;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {

    @Shadow
    @Final
    private Throwable cause;

    @Inject(at = @At("RETURN"), method = "writeToFile")
    private void dark_matter$handleCrash(File file, CallbackInfoReturnable<Boolean> cir) {
        EnvType envType = FabricLoader.getInstance().getEnvironmentType();
        String latestLog = null;
        try {
            latestLog = Files.readString(FabricLoader.getInstance().getGameDir().resolve("logs/latest.log"));
        } catch (IOException ignored) {
        }

        CrashlyticsInternals.handleCrash((CrashReport) (Object) this, this.cause, latestLog, envType);
    }
}
