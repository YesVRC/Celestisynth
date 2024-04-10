package com.aqutheseal.celestisynth.client.renderers.entity.projectile;

import com.aqutheseal.celestisynth.client.models.entity.RainfallTurretModel;
import com.aqutheseal.celestisynth.common.entity.RainfallTurret;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RainfallTurretRenderer  extends GeoEntityRenderer<RainfallTurret> {
    public RainfallTurretRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RainfallTurretModel());
    }
}
