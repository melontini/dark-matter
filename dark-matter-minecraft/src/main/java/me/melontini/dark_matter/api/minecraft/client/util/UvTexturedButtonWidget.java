package me.melontini.dark_matter.api.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class UvTexturedButtonWidget extends ButtonWidget {

    protected final Identifier texture;
    protected final int u;
    protected final int v;
    protected final int hoveredVOffset;
    protected final int textureWidth;
    protected final int textureHeight;

    public UvTexturedButtonWidget(int x, int y, int width, int height, int u, int v, Identifier texture, PressAction pressAction) {
        this(x, y, width, height, u, v, height, texture, 256, 256, pressAction);
    }

    public UvTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, PressAction pressAction) {
        this(x, y, width, height, u, v, hoveredVOffset, texture, 256, 256, pressAction);
    }

    public UvTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, PressAction pressAction) {
        this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, ScreenTexts.EMPTY);
    }

    public UvTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, PressAction pressAction, Text message) {
        super(x, y, width, height, message, pressAction, DEFAULT_NARRATION_SUPPLIER);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.texture = texture;
    }

    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int v = !this.isNarratable() ? this.hoveredVOffset * 2 : this.isSelected() ? this.hoveredVOffset : 0;

        RenderSystem.enableDepthTest();
        context.drawTexture(
                this.texture,
                this.getX(), this.getY(),
                this.u + v, this.v,
                this.width, this.height,
                this.textureWidth, this.textureHeight);
    }
}
