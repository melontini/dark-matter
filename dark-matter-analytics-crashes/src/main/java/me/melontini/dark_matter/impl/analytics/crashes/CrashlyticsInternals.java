package me.melontini.dark_matter.impl.analytics.crashes;

import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.api.analytics.crashes.Crashlytics;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.classes.Tuple;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public final class CrashlyticsInternals {

    private static final Map<String, Tuple<Analytics, Crashlytics.Handler>> HANDLERS = new HashMap<>();
    private static final Map<String, Tuple<Analytics, Crashlytics.Handler>> VIEW = Collections.unmodifiableMap(HANDLERS);

    public static void addHandler(String id, Analytics analytics, Crashlytics.Handler handler) {
        MakeSure.notEmpty(id, "Empty or null id provided!");
        MakeSure.notNulls("Null arguments provided!", analytics, handler);
        HANDLERS.putIfAbsent(id, Tuple.of(analytics, handler));
    }

    public static void removeHandler(String id) {
        MakeSure.notEmpty(id, "Empty or null id provided!");
        HANDLERS.remove(id);
    }

    public static Map<String, Tuple<Analytics, Crashlytics.Handler>> getView() {
        return VIEW;
    }

}
