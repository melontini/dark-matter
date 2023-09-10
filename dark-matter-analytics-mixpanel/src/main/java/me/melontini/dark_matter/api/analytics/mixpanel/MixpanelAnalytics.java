package me.melontini.dark_matter.api.analytics.mixpanel;

import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.api.analytics.Prop;
import me.melontini.dark_matter.api.analytics.mixpanel.interfaces.Mixpanel;
import me.melontini.dark_matter.impl.analytics.mixpanel.MixpanelAnalyticsInternals;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The MixpanelAnalytics class provides methods for sending analytics data to Mixpanel, a cloud-based analytics platform.
 */
public class MixpanelAnalytics {

    private MixpanelAnalytics() {
        throw new UnsupportedOperationException();
    }

    /**
     * Initializes a Handler instance for the provided token and stores it in the MESSAGE_HANDLERS map.
     *
     * @param token The Mixpanel project token to use for sending analytics data.
     * @param eu    A boolean value indicating whether to send analytics data to the Mixpanel EU server or not.
     * @return A MixpanelHandler instance for the provided token.
     */
    public static MixpanelHandler init(Analytics analytics, String token, boolean eu) {
        return MixpanelAnalyticsInternals.init(analytics, token, eu);
    }

    /**
     * Attaches properties to a JSONObject.
     *
     * @param object The JSONObject to attach properties to.
     * @param props  The properties to attach to the JSONObject.
     * @return The modified JSONObject.
     */
    @Contract("_, _ -> param1")
    public static JsonObject attachProps(JsonObject object, Prop @NotNull ... props) {
        for (Prop prop : props) {
            object.addProperty(getPropName(prop), prop.get());
        }
        return object;
    }

    public static String getPropName(Prop prop) {
        return switch (prop) {
            case OS -> "$os";
            case TIMEZONE -> "$timezone";
            case COUNTRY_CODE -> "mp_country_code";
            default -> prop.name().toLowerCase();
        };
    }

    public interface MessageProvider {
        void consume(Mixpanel mixpanel, Analytics analytics);
    }
}
