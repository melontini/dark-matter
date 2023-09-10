package me.melontini.dark_matter.api.analytics.mixpanel;

import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.api.analytics.MessageHandler;
import me.melontini.dark_matter.api.analytics.Prop;
import me.melontini.dark_matter.api.analytics.mixpanel.interfaces.Mixpanel;
import me.melontini.dark_matter.impl.base.DarkMatterLog;

import java.util.concurrent.Future;

public class MixpanelHandler extends MessageHandler<MixpanelAnalytics.MessageProvider> {
    private final Mixpanel mixpanel;

    public MixpanelHandler(Analytics analytics, Mixpanel api) {
        super(analytics);
        this.mixpanel = api;
    }

    protected void sendInternal(MixpanelAnalytics.MessageProvider consumer, boolean wait, boolean errors) {
        if (!analytics.enabled() && !analytics.handleCrashes()) return;
        Future<?> future = EXECUTOR.submit(() -> {
            try {
                consumer.consume(this.mixpanel, this.analytics);
            } catch (Exception e) {
                if (errors) DarkMatterLog.error("Could not send analytics message", e);
            }
        });
        if (wait) {
            try {
                future.get();
            } catch (Exception e) {
                if (errors) DarkMatterLog.error("Could not wait for analytics message", e);
            }
        }
    }

    @Override
    public String getPropName(Prop prop) {
        return MixpanelAnalytics.getPropName(prop);
    }
}
