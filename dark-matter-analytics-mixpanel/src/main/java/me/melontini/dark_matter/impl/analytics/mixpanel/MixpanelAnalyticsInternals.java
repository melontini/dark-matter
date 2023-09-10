package me.melontini.dark_matter.impl.analytics.mixpanel;

import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.api.analytics.mixpanel.MixpanelHandler;
import me.melontini.dark_matter.api.base.util.MakeSure;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class MixpanelAnalyticsInternals {

    public static MixpanelHandler init(Analytics analytics, String token, boolean eu) {
        MakeSure.notEmpty(token, "Invalid token provided! (null/empty)");
        return new MixpanelHandler(analytics, new MixpanelAPI(eu, token));
    }

}
