package me.melontini.dark_matter.impl.analytics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

@ApiStatus.Internal
public class AnalyticsInternals {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ReadConfig CONFIG;
    private static UUID oldID = null;

    static {
        CONFIG = loadConfig();
    }

    private static void upgradeToJson(Config config) {
        Path oldConfigPath = FabricLoader.getInstance().getConfigDir().resolve("dark-matter/analytics.properties");
        if (Files.exists(oldConfigPath)) {
            Properties properties = new Properties();
            try {
                properties.load(Files.newInputStream(oldConfigPath));
                config.enabled = Boolean.parseBoolean(properties.getProperty("enabled"));
                config.userUUID = UUID.fromString(properties.getProperty("user_id"));

                Files.deleteIfExists(oldConfigPath);
            } catch (IOException e) {
                DarkMatterLog.error("Could not read analytics properties", e);
            }
        }
    }

    private static ReadConfig loadConfig() {
        Config config = new Config();
        upgradeToJson(config);
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("dark-matter/analytics.json");
        if (Files.exists(configPath)) {
            try(var reader = Files.newBufferedReader(configPath)) {
                config = GSON.fromJson(reader, Config.class);
                oldID = config.userUUID;
                Files.write(configPath, GSON.toJson(config).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Files.createDirectories(configPath.getParent());
                Files.write(configPath, GSON.toJson(config).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ReadConfig(config.enabled,  config.crashesEnabled);
    }

    public static boolean isEnabled() {
        return CONFIG.enabled();
    }

    public static boolean handleCrashes() {
        return CONFIG.crashesEnabled();
    }

    public static Optional<UUID> getOldID() {
        return Optional.ofNullable(oldID);
    }

    public static void init() {
        // Init the config on PreLaunch.
    }

    private record ReadConfig(boolean enabled, boolean crashesEnabled) {

    }

    private static class Config {
        public boolean enabled = true;
        public boolean crashesEnabled = true;
        @Deprecated
        public UUID userUUID;
    }
}
