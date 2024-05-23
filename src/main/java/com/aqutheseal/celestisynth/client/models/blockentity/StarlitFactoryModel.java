package com.aqutheseal.celestisynth.client.models.blockentity;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.common.block.CelestialCraftingTableBlockEntity;
import com.aqutheseal.celestisynth.common.block.StarlitFactoryBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StarlitFactoryModel extends GeoModel<StarlitFactoryBlockEntity> {
    @Override
    public ResourceLocation getAnimationResource(StarlitFactoryBlockEntity animatable) {
        return Celestisynth.prefix("animations/starlit_factory.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(StarlitFactoryBlockEntity animatable) {
        return Celestisynth.prefix("geo/starlit_factory.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StarlitFactoryBlockEntity entity) {
        return Celestisynth.prefix("textures/block/starlit_factory.png");
    }
}
