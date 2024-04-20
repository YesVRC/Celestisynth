package com.aqutheseal.celestisynth.client.renderers.misc;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.api.mixin.PlayerMixinSupport;
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

public class CSGuiRenderer {
    public static final ResourceLocation KERES_SACRIFICE = Celestisynth.prefix("textures/misc/keres_sacrifice.png");

    public final RenderGuiEvent event;
    public final GuiGraphics gui;
    public final PoseStack pose;
    public final Matrix4f poseMatrix;
    public final MultiBufferSource.BufferSource buffer;
    public final Font font;

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
        if (Minecraft.getInstance().player instanceof PlayerMixinSupport mixinPlayer) {
            if (mixinPlayer.getPulseScale() > 0) {
                Color pulse = Color.ofRGBA(255, 255, 255, Mth.clamp(mixinPlayer.getPulseScale(), 0, 255));
                gui.fill(0, 0, getWidth(), getHeight(), pulse.argbInt());
            }

            int pointerX = (getWidth() / 2);
            int pointerY = (getHeight() / 2) + 10;
            if (mixinPlayer.getChantMark() < 20 && !mixinPlayer.getChantMessage().isEmpty()) {
                Component text = Component.translatable(mixinPlayer.getChantMessage());
                int textLength = font.width(text.getVisualOrderText());

                int lerp = (int) Mth.lerp(mixinPlayer.getChantMark() / 20F, 255F, 0);
                Color base = new Color(mixinPlayer.getChantColor());
                Color pulse = Color.ofRGBA(base.getRed(), base.getGreen(), base.getBlue(), Mth.clamp(lerp, 0, 255));
                Color modifiedPulse = pulse.darker(5);
                font.drawInBatch8xOutline(text.getVisualOrderText(), pointerX - (float) textLength / 2, pointerY, pulse.argbInt(), modifiedPulse.argbInt(), poseMatrix, buffer, LightTexture.FULL_BRIGHT);

                int lerpBack = (int) Mth.lerp(mixinPlayer.getChantMark() / 20F, 10F, 0);
                Color back = Color.ofRGBA(0, 0, 0,  Mth.clamp(lerpBack, 0, 255));
                event.getGuiGraphics().fill((int) (pointerX - textLength / 1.5), pointerY - 2, (int) (pointerX + textLength / 1.5), pointerY + 10, back.argbInt());
            }
        }
    }

    public void renderGuiAdditionsPre() {
    }

    public void renderGuiAdditionsPost() {
        if (Minecraft.getInstance().player instanceof PlayerMixinSupport mixinPlayer) {
            if (mixinPlayer.getKeresMark() < 20) {
                float lerp = Mth.lerp(mixinPlayer.getKeresMark() / 20F, 0.5F, 0);
                render256x256(KERES_SACRIFICE, gui, getWidth(), getHeight(), lerp);
            }
        }
    }

    public void render256x256(ResourceLocation texture, GuiGraphics gui, int width, int height, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        gui.setColor(1.0F, 1.0F, 1.0F, alpha);
        gui.blit(texture, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
