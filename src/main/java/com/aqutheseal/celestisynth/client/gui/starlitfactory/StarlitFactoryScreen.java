package com.aqutheseal.celestisynth.client.gui.starlitfactory;

import com.aqutheseal.celestisynth.Celestisynth;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import software.bernie.geckolib.core.object.Color;

import java.util.ArrayList;

public class StarlitFactoryScreen extends AbstractContainerScreen<StarlitFactoryMenu> {
    public static final ResourceLocation FACTORY_GUI = Celestisynth.prefix("textures/gui/starlit_factory.png");
    private final ArrayList<FactoryStar> stars = new ArrayList<>();

    public StarlitFactoryScreen(StarlitFactoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        assert minecraft.level != null;
        RandomSource random = minecraft.level.random;
        if (random.nextInt(50) == 0) {
            stars.add(new FactoryStar(-50 - random.nextInt(700), -50 - random.nextInt(700), 0.5 + random.nextDouble(), 0.5 + random.nextDouble()));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        assert Minecraft.getInstance().player != null;
        float motion = Mth.sin((float) (Minecraft.getInstance().player.tickCount * 0.2)) * 1;
        Color color = Color.ofRGBA(0.75F + (motion * 0.25F), 0.75F + (motion * 0.25F), 1F, 1F);
        this.font.drawInBatch8xOutline(this.title.getVisualOrderText(), this.titleLabelX - 5, this.titleLabelY - 15, color.argbInt(), color.darker(5F).getColor(), pGuiGraphics.pose().last().pose(), pGuiGraphics.bufferSource(), LightTexture.FULL_BRIGHT);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        assert Minecraft.getInstance().player != null;
        float motion = Mth.sin((float) (Minecraft.getInstance().player.tickCount * 0.05)) * 1;
        float motion2 = Mth.cos((float) (Minecraft.getInstance().player.tickCount * 0.05)) * 1;

        for (FactoryStar star : this.stars) {
            star.render(pGuiGraphics);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(0.95F + (motion * 0.05F), 0.95F + (motion2 * 0.05F), 1.0F, 1.0F);

        pGuiGraphics.blit(FACTORY_GUI, super.leftPos, super.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int energyBurnTime = menu.data.get(0);
        int energyAmount = menu.data.get(1);
        int factoryForgeTime = menu.data.get(2);
        int maxFactoryForgeTime = menu.data.get(3);

        boolean isHoldingValidRecipe = menu.data.get(4) == 1;

        double burnProgress = (double) energyBurnTime / 100;
        pGuiGraphics.blit(FACTORY_GUI, super.leftPos + 135, super.topPos + 30, 193, 16, (int) (burnProgress * 14), 2);

        double energyAmountProgress = (double) energyAmount / 1000;
        int vOffset = (int) (52 - (energyAmountProgress * 52));
        pGuiGraphics.blit(FACTORY_GUI, super.leftPos + 152, super.topPos + 17 + vOffset, 176,  vOffset, 16, (int) (energyAmountProgress * 52));

        double forgeProgress = (double) factoryForgeTime / maxFactoryForgeTime;
        pGuiGraphics.blit(FACTORY_GUI, super.leftPos + 62, super.topPos + 3, 208, 0, 44, (int) (forgeProgress * 75));

        if (isHoldingValidRecipe) {
            pGuiGraphics.blit(FACTORY_GUI, super.leftPos + 26, super.topPos + 34, 192, 0, 16, 15);
        }
    }

    public static class FactoryStar {
        private final int centerX;
        private final int centerY;
        private final double speedX;
        private final double speedY;

        public float scale;
        public int tickCount;

        public FactoryStar(int centerX, int centerY, double speedX, double speedY) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.speedX = speedX;
            this.speedY = speedY;
            this.scale = 1F + Minecraft.getInstance().player.getRandom().nextFloat() * 0.5F;
        }

        public void render(GuiGraphics pGuiGraphics) {
            tickCount++;
            assert Minecraft.getInstance().player != null;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F + Mth.sin(tickCount * 0.005F) * 0.5F);
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().scale(scale, scale, scale);
            pGuiGraphics.blit(FACTORY_GUI, (int) (centerX + (tickCount * speedX)), (int) (centerY + (tickCount * speedY)), 192, 32, 16, 16);
            pGuiGraphics.pose().popPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
        }
    }
}
