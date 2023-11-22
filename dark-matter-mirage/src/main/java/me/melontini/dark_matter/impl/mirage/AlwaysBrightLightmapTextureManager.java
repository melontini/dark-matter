package me.melontini.dark_matter.impl.mirage;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;

public class AlwaysBrightLightmapTextureManager extends LightmapTextureManager {

    public static final AlwaysBrightLightmapTextureManager INSTANCE = new AlwaysBrightLightmapTextureManager();

    private AlwaysBrightLightmapTextureManager() {
        super(MinecraftClient.getInstance().gameRenderer, MinecraftClient.getInstance());
    }

    @Override
    public void update(float delta) {
        //no updates for you
    }

}
