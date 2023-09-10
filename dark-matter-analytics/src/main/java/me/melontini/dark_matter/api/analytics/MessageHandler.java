package me.melontini.dark_matter.api.analytics;

import me.melontini.dark_matter.api.base.util.MakeSure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class defines a handler for sending analytics messages to a backend.
 *
 * @param <T> The type of message consumer that this handler will send messages to.
 */
public abstract class MessageHandler<T> {

    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> new Thread(r, "Analytics-Thread"));

    protected final Analytics analytics;

    public MessageHandler(Analytics analytics) {
        MakeSure.notNull(analytics, "null analytics provided");
        this.analytics = analytics;
    }

    public final void send(T consumer, boolean wait, boolean errors) {
        MakeSure.notNull(consumer, "null consumer provided");
        if (analytics.enabled() || analytics.handleCrashes()) sendInternal(consumer, wait, errors);
    }

    public final void send(T consumer, boolean wait) {
        MakeSure.notNull(consumer, "null consumer provided");
        if (analytics.enabled() || analytics.handleCrashes()) sendInternal(consumer, wait, false);
    }

    public final void send(T consumer) {
        MakeSure.notNull(consumer, "null consumer provided");
        if (analytics.enabled() || analytics.handleCrashes()) sendInternal(consumer, false, false);
    }

    protected abstract void sendInternal(T consumer, boolean wait, boolean errors);

    public String getPropName(Prop prop) {
        return prop.name().toLowerCase();
    }
}
