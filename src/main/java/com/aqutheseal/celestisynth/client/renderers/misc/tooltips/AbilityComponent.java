package com.aqutheseal.celestisynth.client.renderers.misc.tooltips;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.util.ExtraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;
import software.bernie.geckolib.core.object.Color;

public class AbilityComponent {

    public record Data(String itemName, int totalAbilityAmount, int highlightedAbilityIndex, Side side) implements TooltipComponent {
    }

    public record Renderer(Data data) implements ClientTooltipComponent {
        public static final int DESC_WRAP_WIDTH = 320;
        public static final int ICON_HEIGHT = 16;
        public static final int ICON_OFFSETED_HEIGHT = ICON_HEIGHT + 8;

        @Override
        public int getHeight() {
            return this.descriptionWordWrapHeight() + this.getConditionHeight() + ICON_OFFSETED_HEIGHT + 5;
        }

        @Override
        public int getWidth(Font pFont) {
            return (int) (DESC_WRAP_WIDTH * 0.8);
        }

        @Override
        public void renderText(Font pFont, int pMouseX, int pMouseY, Matrix4f pMatrix, MultiBufferSource.BufferSource pBufferSource) {

            Matrix4f copyMatrix = new Matrix4f(pMatrix);
            copyMatrix.translate(pMouseX, pMouseY, 0);
            copyMatrix.scale(0.8F);
            copyMatrix.translate(-pMouseX, -pMouseY, 0);

            if (data.side() == Side.SKILL) {
                pFont.drawInBatch(this.highlightedCondition(), pMouseX, pMouseY + ICON_OFFSETED_HEIGHT + 4, -1, true, copyMatrix, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            }

            pFont.drawInBatch(this.highlightedName(), pMouseX, pMouseY + this.getConditionHeight() + ICON_OFFSETED_HEIGHT, -1, true, pMatrix, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            this.drawWordWrap(pFont, this.highlightedDescription(), pMouseX, pMouseY + this.getConditionHeight() + ICON_OFFSETED_HEIGHT + 10, DESC_WRAP_WIDTH, -1, pMatrix, pBufferSource);
        }

        @Override
        public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
            int order = 0;
            for (int i = 1; i < data.totalAbilityAmount() + 1; i++) {
                if (i != data.highlightedAbilityIndex()) {
                    pGuiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
                }
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(pX + order, pY, 0);
                pGuiGraphics.pose().scale(0.65F, 0.65F, 0.65F);
                pGuiGraphics.pose().translate(-pX + order, -pY, 0);
                pGuiGraphics.blit(Celestisynth.prefix("textures/" + data.side().getAsText() + "icon/" + data.itemName() + "_" + data.side().getAsText() + "_icon_" + i + ".png"), pX + order, pY - 4, 0, 0, 32, 32, 32, 32);
                pGuiGraphics.pose().popPose();

                pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                order += 12;
            }
            pGuiGraphics.fill(pX - 10, pY + ICON_OFFSETED_HEIGHT - 3, pX + this.getWidth(pFont) + 10, pY + this.getConditionHeight() + ICON_OFFSETED_HEIGHT + this.descriptionWordWrapHeight() + 9, Color.BLACK.argbInt());
        }

        public int getConditionHeight() {
            return data.side() == Side.SKILL ? 7 : 0;
        }

        public Component highlightedName() {
            int tickCount = (int) (Minecraft.getInstance().player.tickCount * 0.5);
            return Component.translatable("item.celestisynth." + data.itemName() + "." + data.side().getAsText() + "_" + data.highlightedAbilityIndex()).withStyle(Style.EMPTY.withColor(ExtraUtil.getCelestialColor(tickCount).argbInt()));
        }

        public Component highlightedDescription() {
            return Component.translatable("item.celestisynth." + data.itemName() + ".desc_" + data.highlightedAbilityIndex()).withStyle(Style.EMPTY.withColor(Color.GRAY.argbInt()));
        }

        public Component highlightedCondition() {
            return Component.translatable("item.celestisynth." + data.itemName() + ".condition_" + data.highlightedAbilityIndex()).withStyle(Style.EMPTY.withItalic(true).withColor(Color.DARK_GRAY.argbInt()));
        }

        public int descriptionWordWrapHeight() {
            return Minecraft.getInstance().font.wordWrapHeight(this.highlightedDescription(), DESC_WRAP_WIDTH);
        }

        public void drawWordWrap(Font pFont, FormattedText pText, int pX, int pY, int pLineWidth, int pColor, Matrix4f pMatrix, MultiBufferSource.BufferSource pBufferSource) {
            for(FormattedCharSequence formattedcharsequence : pFont.split(pText, pLineWidth)) {
                Matrix4f copyMatrix = new Matrix4f(pMatrix);
                copyMatrix.translate(pX, pY, 0);
                copyMatrix.scale(0.8F);
                copyMatrix.translate(-pX, -pY, 0);
                pFont.drawInBatch(formattedcharsequence, pX, pY, pColor, true, copyMatrix, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                pY += 9;
            }

        }
    }

    public enum Side {
        SKILL("skill"),
        PASSIVE("passive");

        public final String asText;

        Side(String asText) {
            this.asText = asText;
        }

        public String getAsText() {
            return asText;
        }
    }
}
