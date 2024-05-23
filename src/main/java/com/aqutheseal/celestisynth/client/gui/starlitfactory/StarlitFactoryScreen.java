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
import net.minecraft.world.entity.player.Inventory;
import software.bernie.geckolib.core.object.Color;

public class StarlitFactoryScreen extends AbstractContainerScreen<StarlitFactoryMenu> {
    private static final ResourceLocation FACTORY_GUI = Celestisynth.prefix("textures/gui/starlit_factory.png");

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
        float motion = Mth.sin((float) (Minecraft.getInstance().player.tickCount * 0.05)) * 1;
        float motion2 = Mth.cos((float) (Minecraft.getInstance().player.tickCount * 0.05)) * 1;
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
}
