package com.aqutheseal.celestisynth.client.renderers.entity.projectile;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.client.models.entity.projectile.RainfallLaserModel;
import com.aqutheseal.celestisynth.common.entity.projectile.RainfallLaserMarker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RainfallLaserRenderer extends EntityRenderer<RainfallLaserMarker> {
    private static final ResourceLocation ARROW_TEXTURE = Celestisynth.prefix("textures/entity/projectile/rainfall_arrow.png");
    private final RainfallLaserModel<RainfallLaserMarker> model;

    public RainfallLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new RainfallLaserModel<>(context.bakeLayer(RainfallLaserModel.LAYER_LOCATION));
    }

    @Override
    public boolean shouldRender(RainfallLaserMarker pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }

    @Override
    public void render(RainfallLaserMarker entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        Vec3 originPos = entity.getOrigin();
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Origin: " + originPos.toString()), true);
        double distanceCalculated = entity.position().distanceTo(originPos) * 0.5;
        poseStack.pushPose();
        float scale = 0.8f;
        float progress = (float) entity.tickCount / 7;
        float size = Mth.clampedLerp(scale, 0, progress);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - (90.0F)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + (90.0F)));
        poseStack.translate(2.5, 0, 0);
        for (double i = 0; i < distanceCalculated - 2; i = i + 2.255) {
            poseStack.pushPose();
            poseStack.scale(size,  2, size);
            poseStack.translate(0, i, 0);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(ARROW_TEXTURE));
            this.model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1, 1, entity.isQuasar() ? 1 : 0, Mth.clampedLerp(1, 0, progress));
            VertexConsumer consumer2 = buffer.getBuffer(RenderType.entityTranslucentEmissive(ARROW_TEXTURE));
            this.model.renderToBuffer(poseStack, consumer2, light, OverlayTexture.NO_OVERLAY, 1, 1, entity.isQuasar() ? 1 : 0, Mth.clampedLerp(0.5F, 0, progress));
            poseStack.popPose();
        }
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, light);
    }

    @Override
    protected int getSkyLightLevel(RainfallLaserMarker pEntity, BlockPos pPos) {
        return 15;
    }

    @Override
    protected int getBlockLightLevel(RainfallLaserMarker pEntity, BlockPos pPos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(RainfallLaserMarker entity) {
        return ARROW_TEXTURE;
    }
}
