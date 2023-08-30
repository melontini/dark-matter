package me.melontini.dark_matter.impl.mirage;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoading;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

@ApiStatus.Internal
public class FakeWorld extends ClientWorld {

    public static ClientWorld INSTANCE;

    static {
        DarkMatterLog.info("Creating a fake ClientWorld. Hold tight!");

        var isl = MinecraftClient.getInstance().createIntegratedServerLoader();

        LevelStorage.Session session = isl.createSession("dark_matter_fake_world_please_ignore");
        ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
        DataConfiguration dataConfiguration = MinecraftServer.DEMO_LEVEL_INFO.getDataConfiguration();
        SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, dataConfiguration, true, false);

        try {
            Pair<DataConfiguration, LifecycledResourceManager> pair = dataPacks.load();
            LifecycledResourceManager lifecycledResourceManager = pair.getSecond();

            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = ServerDynamicRegistryType.createCombinedDynamicRegistries();
            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries2 = SaveLoading.withRegistriesLoaded(lifecycledResourceManager, combinedDynamicRegistries, ServerDynamicRegistryType.WORLDGEN, RegistryLoader.DYNAMIC_REGISTRIES);

            DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries2.getPrecedingRegistryManagers(ServerDynamicRegistryType.DIMENSIONS);
            DynamicRegistryManager.Immutable immutable2 = RegistryLoader.load(lifecycledResourceManager, immutable, RegistryLoader.DIMENSION_REGISTRIES);

            DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = WorldPresets.createDemoOptions(immutable).toConfig(immutable2.get(RegistryKeys.DIMENSION));
            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries3 = combinedDynamicRegistries2.with(ServerDynamicRegistryType.DIMENSIONS, dimensionsConfig.toDynamicRegistryManager());

            ClientPlayNetworkHandler networkHandler = new ClientPlayNetworkHandler(MinecraftClient.getInstance(), new ClientConnection(NetworkSide.CLIENTBOUND),
                    new ClientConnectionState(
                            new GameProfile(UUID.randomUUID(), "fake_profile_ratio"),
                            null, new DynamicRegistryManager.ImmutableImpl(SerializableRegistries.streamDynamicEntries(combinedDynamicRegistries3)).toImmutable(),
                            FeatureSet.of(FeatureFlags.VANILLA), null, null, null));

            INSTANCE = new FakeWorld(networkHandler,
                    new Properties(Difficulty.EASY, false, false), World.OVERWORLD, combinedDynamicRegistries3.getPrecedingRegistryManagers(ServerDynamicRegistryType.DIMENSIONS).get(RegistryKeys.DIMENSION_TYPE).entryOf(DimensionTypes.OVERWORLD), 0, 0, null, MinecraftClient.getInstance().worldRenderer, true, 0);

            Path path = FabricLoader.getInstance().getGameDir().resolve("saves/dark_matter_fake_world_please_ignore");
            if (Files.exists(path)) {
                FileUtils.deleteDirectory(path.toFile());
            }
        } catch (IOException e) {
            DarkMatterLog.warn("Couldn't delete FakeWorld's directory.", e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private FakeWorld(ClientPlayNetworkHandler networkHandler, Properties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimensionTypeEntry, int loadDistance, int simulationDistance, Supplier<Profiler> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed) {
        super(networkHandler, properties, registryRef, dimensionTypeEntry, loadDistance, simulationDistance, profiler, worldRenderer, debugWorld, seed);
    }

    public static void init() {

    }

}