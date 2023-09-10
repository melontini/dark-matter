package me.melontini.dark_matter.api.analytics;

import me.melontini.dark_matter.impl.analytics.AnalyticsImpl;
import me.melontini.dark_matter.impl.analytics.AnalyticsInternals;
import net.fabricmc.loader.api.ModContainer;

import java.util.Optional;
import java.util.UUID;


public interface Analytics {

    UUID nullID = new UUID(0, 0);

    /**
     * Get the analytics instance for the given mod.
     * IDs provided by this method are unique per game instance, per mod.
     * <p>
     * The default implementation pulls its config from {@code config/dark-matter/analytics.json} and {@code .dark-matter/analytics}
     */
    static Analytics get(ModContainer mod) {
        return new AnalyticsImpl(mod, true);
    }

    static Analytics get(ModContainer mod, boolean init) {
        return new AnalyticsImpl(mod, init);
    }

    /**
     * Could be used for sending anonymous/pseudonymous events.
     * @return The default/null UUID for analytics.
     */
    default UUID getDefaultUUID() {
        return nullID;
    }

    default String getDefaultUUIDString() {
        return getDefaultUUID().toString();
    }

    /**
     * The unique ID given to this game instance.
     * @return Either the unique UUID or the default UUID if disabled by the user.
     */
    UUID getUUID();

    default String getUUIDString() {
        return getUUID().toString();
    }

    default boolean isEnabled() {
        return enabled();
    }

    boolean enabled();

    boolean handleCrashes();

    /**
     * Nuke profiles using the old ID.
     * @return Pre-2.0.0 UUID.
     * @deprecated 2.0.0
     */
    @Deprecated(since = "2.0.0")
    static Optional<UUID> oldUUID() {
        return AnalyticsInternals.getOldID();
    }
}
