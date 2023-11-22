package me.melontini.dark_matter.impl.analytics.crashes;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.api.analytics.crashes.Crashlytics;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.classes.Tuple;
import net.fabricmc.api.EnvType;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class CrashlyticsInternals {

    private static final Map<String, Tuple<Analytics, Crashlytics.Handler>> HANDLERS = new HashMap<>();

    public static void addHandler(String id, Analytics analytics, Crashlytics.Handler handler) {
        MakeSure.notEmpty(id, "Empty or null id provided!");
        MakeSure.notNulls("Null arguments provided!", analytics, handler);
        HANDLERS.putIfAbsent(id, Tuple.of(analytics, handler));
    }

    public static void removeHandler(String id) {
        MakeSure.notEmpty(id, "Empty or null id provided!");
        HANDLERS.remove(id);
    }

    public static void handleCrash(CrashReport report, Throwable cause, String latestLog, EnvType envType) {
        for (Tuple<Analytics, Crashlytics.Handler> tuple : HANDLERS.values()) {
            if (tuple.left().handleCrashes()) {
                tuple.right().handle(report, cause, latestLog, envType);
            }
        }
    }
}
