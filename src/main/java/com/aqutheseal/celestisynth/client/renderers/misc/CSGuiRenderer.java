package com.aqutheseal.celestisynth.client.renderers.misc;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.api.mixin.PlayerMixinSupport;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderGuiEvent;
import org.joml.Matrix4f;
import software.bernie.geckolib.core.object.Color;

import java.util.ArrayList;
import java.util.List;

public class CSGuiRenderer {
    public RenderGuiEvent event;
    public GuiGraphics gui;
    public PoseStack pose;
    public Matrix4f poseMatrix;
    public MultiBufferSource.BufferSource buffer;
    public Font font;

    public CSGuiRenderer(RenderGuiEvent event) {
        this.event = event;
        this.gui = event.getGuiGraphics();
        this.pose = gui.pose();
        this.poseMatrix = pose.last().pose();
        this.buffer = gui.bufferSource();
        this.font = Minecraft.getInstance().font;
    }

    public int getWidth() {
        return event.getWindow().getWidth() / 2;
    }

    public int getHeight() {
        return event.getWindow().getHeight() / 2;
    }

    public void renderGuiAdditions() {
        if (Minecraft.getInstance().player instanceof PlayerMixinSupport mixinPlayer && !Minecraft.getInstance().isPaused()) {
            if (mixinPlayer.getPulseScale() > 0) {
                Color pulse = Color.ofRGBA(255, 255, 255, Mth.clamp(mixinPlayer.getPulseScale(), 0, 255));
                gui.fill(0, 0, getWidth(), getHeight(), pulse.argbInt());
            }
        }
    }

    public void renderGuiAdditionsPre() {
    }

    public void renderGuiAdditionsPost() {
        if (Minecraft.getInstance().player instanceof PlayerMixinSupport mixinPlayer && !Minecraft.getInstance().isPaused()) {
            if (mixinPlayer.getTexturePulseMark() < 20 && !mixinPlayer.getTexturePulseImage().isEmpty()) {
                float lerp = Mth.lerp(mixinPlayer.getTexturePulseMark() / 20F, 0.5F, 0);
                ResourceLocation location = Celestisynth.prefix("textures/misc/" + mixinPlayer.getTexturePulseImage() + ".png");

                if (this.allowVignetteList().contains(mixinPlayer.getTexturePulseImage())) {
                    renderVignette(gui, getWidth(), getHeight(), lerp * 0.5F);
                }

                render256x256(location, gui, getWidth(), getHeight(), 1.0F, 0.1F, 0.2F, lerp);
            }

            int pointerX = (getWidth() / 2);
            int pointerY = (getHeight() / 2);
            if (mixinPlayer.getChantMark() < 20 && !mixinPlayer.getChantMessage().isEmpty()) {
                Component text = Component.translatable(mixinPlayer.getChantMessage());
                int textLength = font.width(text.getVisualOrderText());

                int lerp = (int) Mth.lerp(mixinPlayer.getChantMark() / 20F, 255F, 0);
                Color base = new Color(mixinPlayer.getChantColor());
                Color pulse = Color.ofRGBA(base.getRed(), base.getGreen(), base.getBlue(), Mth.clamp(lerp, 0, 255));
                Color modifiedPulse = pulse.darker(5);

                float txt = textLength / 2F;
                float xOffset = -(getWidth() / 8F) * 1.3F;
                float yOffset = -((getHeight() / 8F) * 1.3F) + 7.5F;
                font.drawInBatch8xOutline(text.getVisualOrderText(), pointerX + xOffset - txt, pointerY + yOffset, pulse.argbInt(), modifiedPulse.argbInt(), poseMatrix.scale(1.5F), buffer, LightTexture.FULL_BRIGHT);
                poseMatrix.normal().scale(1.5F);
            }
        }
    }

    public List<String> allowVignetteList() {
        List<String> whitelist = new ArrayList<>();
        whitelist.add("keres_sacrifice_0");
        whitelist.add("keres_sacrifice_1");
        whitelist.add("keres_sacrifice_2");
        whitelist.add("keres_sacrifice_3");
        return whitelist;
    }

    public void render256x256(ResourceLocation texture, GuiGraphics gui, int width, int height, float red, float green, float blue, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        gui.setColor(red, green, blue, alpha);
        gui.blit(texture, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
    }

    public void renderVignette(GuiGraphics gui, int width, int height, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        gui.setColor(1.0F, 1.0F, 1.0F, alpha);
        gui.blit(new ResourceLocation("textures/misc/vignette.png"), 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
    }
}
