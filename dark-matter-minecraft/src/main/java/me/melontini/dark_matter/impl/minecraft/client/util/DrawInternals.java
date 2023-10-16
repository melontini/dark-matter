package me.melontini.dark_matter.impl.minecraft.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public class DrawInternals {

    public static final Lazy<FakeScreen> FAKE_SCREEN = Lazy.of(() -> FakeScreen::new);

    private DrawInternals() {
        throw new UnsupportedOperationException();
    }

    public static FakeScreen getFakeScreen() {
        return FAKE_SCREEN.get();
    }

    public static void renderTooltip(DrawContext context, ItemStack stack, float x, float y) {
        context.getMatrices().push();
        context.getMatrices().translate(x - (int) x, y - (int) y, 0);
        context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, stack, (int) x, (int) y);
        context.getMatrices().pop();
    }

    public static void renderTooltip(DrawContext context, Text text, float x, float y) {
        context.getMatrices().push();
        context.getMatrices().translate(x - (int) x, y - (int) y, 0);
        context.drawTooltip(MinecraftClient.getInstance().textRenderer, text, (int) x, (int) y);
        context.getMatrices().pop();
    }

    public static void renderTooltip(DrawContext context, List<Text> lines, float x, float y) {
        context.getMatrices().push();
        context.getMatrices().translate(x - (int) x, y - (int) y, 0);
        context.drawTooltip(MinecraftClient.getInstance().textRenderer, lines, (int) x, (int) y);
        context.getMatrices().pop();
    }

    public static void renderTooltip(DrawContext context, List<Text> lines, Optional<TooltipData> data, float x, float y) {
        context.getMatrices().push();
        context.getMatrices().translate(x - (int) x, y - (int) y, 0);
        context.drawTooltip(MinecraftClient.getInstance().textRenderer, lines, data, (int) x, (int) y);
        context.getMatrices().pop();
    }

    public static void renderOrderedTooltip(DrawContext context, List<? extends OrderedText> lines, float x, float y) {
        context.getMatrices().push();
        context.getMatrices().translate(x - (int) x, y - (int) y, 0);
        context.drawOrderedTooltip(MinecraftClient.getInstance().textRenderer, lines, (int) x, (int) y);
        context.getMatrices().pop();
    }

    public static void renderTooltipFromComponents(DrawContext context, List<TooltipComponent> components, float x, float y) {
        context.getMatrices().push();
        context.getMatrices().translate(x - (int) x, y - (int) y, 0);
        context.drawTooltip(MinecraftClient.getInstance().textRenderer, components, (int) x, (int) y, HoveredTooltipPositioner.INSTANCE);
        context.getMatrices().pop();
    }

    public static void renderTooltipFromComponents(DrawContext context, List<TooltipComponent> components, float x, float y, TooltipPositioner positioner) {
        context.getMatrices().push();
        context.getMatrices().translate(x - (int) x, y - (int) y, 0);
        context.drawTooltip(MinecraftClient.getInstance().textRenderer, components, (int) x, (int) y, positioner);
        context.getMatrices().pop();
    }

    public static void fillGradient(MatrixStack matrices, float startX, float startY, float endX, float endY, float z, int colorStart, int colorEnd) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getPositionMatrix(), bufferBuilder, startX, startY, endX, endY, z, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    public static void fillGradient(Matrix4f matrix, BufferBuilder builder, float startX, float startY, float endX, float endY, float z, int colorStart, int colorEnd) {
        float f = ColorUtil.getAlphaF(colorStart), g = ColorUtil.getRedF(colorStart), h = ColorUtil.getGreenF(colorStart), i = ColorUtil.getBlueF(colorStart);
        float j = ColorUtil.getAlphaF(colorEnd), k = ColorUtil.getRedF(colorEnd), l = ColorUtil.getGreenF(colorEnd), m = ColorUtil.getBlueF(colorEnd);
        builder.vertex(matrix, endX, startY, z).color(g, h, i, f).next();
        builder.vertex(matrix, startX, startY, z).color(g, h, i, f).next();
        builder.vertex(matrix, startX, endY, z).color(k, l, m, j).next();
        builder.vertex(matrix, endX, endY, z).color(k, l, m, j).next();
    }

    public static void fillGradientHorizontal(MatrixStack matrices, float startX, float startY, float endX, float endY, int colorStart, int colorEnd, float z) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradientHorizontal(matrices.peek().getPositionMatrix(), bufferBuilder, startX, startY, endX, endY, z, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    public static void fillGradientHorizontal(Matrix4f matrix, BufferBuilder builder, float startX, float startY, float endX, float endY, float z, int colorStart, int colorEnd) {
        float f = ColorUtil.getAlphaF(colorStart), g = ColorUtil.getRedF(colorStart), h = ColorUtil.getGreenF(colorStart), i = ColorUtil.getBlueF(colorStart);
        float j = ColorUtil.getAlphaF(colorEnd), k = ColorUtil.getRedF(colorEnd), l = ColorUtil.getGreenF(colorEnd), m = ColorUtil.getBlueF(colorEnd);
        builder.vertex(matrix, endX, startY, z).color(k, l, m, j).next();
        builder.vertex(matrix, startX, startY, z).color(g, h, i, f).next();
        builder.vertex(matrix, startX, endY, z).color(g, h, i, f).next();
        builder.vertex(matrix, endX, endY, z).color(k, l, m, j).next();
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float z, float u, float v, float width, float height) {
        drawTexture(matrices, x, y, z, u, v, width, height, 256, 256);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float z, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, float textureWidth, float textureHeight) {
        drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        drawTexture(matrices, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTexture(MatrixStack matrices, float x0, float x1, float y0, float y1, float z, float regionWidth, float regionHeight, float u, float v, float textureWidth, float textureHeight) {
        drawTexturedQuad(matrices.peek().getPositionMatrix(), x0, x1, y0, y1, z, (u + 0.0F) / textureWidth, (u + regionWidth) / textureWidth, (v + 0.0F) / textureHeight, (v + regionHeight) / textureHeight);
    }

    public static void drawTexturedQuad(Matrix4f matrix, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x0, y1, z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, x1, y0, z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, x0, y0, z).texture(u0, v0).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public static void renderGuiItemModelCustomMatrixNoTransform(MatrixStack matrixStack, ItemStack stack, BakedModel model) {
        MinecraftClient client = MinecraftClient.getInstance();

        client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        client.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, matrixStack, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }
    }

    public static void renderGuiItemModelCustomMatrix(MatrixStack matrixStack, ItemStack stack, float x, float y, BakedModel model) {
        MinecraftClient client = MinecraftClient.getInstance();

        client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.push();
        matrixStack.translate(x, y, 100.0F);
        matrixStack.translate(8.0, 8.0, 0.0);
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.scale(16.0F, 16.0F, 16.0F);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        client.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, matrixStack, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrixStack.pop();
    }

    public static class FakeScreen extends Screen {

        protected FakeScreen() {
            super(null);
            this.reset(MinecraftClient.getInstance(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        }

        public void reset(MinecraftClient client, int width, int height) {
            this.client = client;
            this.textRenderer = client.textRenderer;
            this.width = width;
            this.height = height;
            this.clearAndInit();
        }
    }
}
