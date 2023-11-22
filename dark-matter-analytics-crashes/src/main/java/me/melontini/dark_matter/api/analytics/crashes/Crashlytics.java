package me.melontini.dark_matter.api.analytics.crashes;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.impl.analytics.crashes.CrashlyticsInternals;
import net.fabricmc.api.EnvType;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public final class Crashlytics {

    public static void addHandler(String id, Analytics analytics, Handler handler) {
        CrashlyticsInternals.addHandler(id, analytics, handler);
    }

    public static void removeHandler(String id) {
        CrashlyticsInternals.removeHandler(id);
    }

    @FunctionalInterface
    public interface Handler {
        void handle(CrashReport report, Throwable cause, @Nullable String latestLog, EnvType envType);
    }
}
