package com.aqutheseal.celestisynth.client.models.entity.mob;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.common.entity.mob.misc.StarMonolith;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StarMonolithModel extends GeoModel<StarMonolith> {
    @Override
    public ResourceLocation getModelResource(StarMonolith animatable) {
        return Celestisynth.prefix("geo/mob/star_monolith.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StarMonolith animatable) {
        return Celestisynth.prefix("textures/entity/mob/star_monolith/star_monolith_nether.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StarMonolith animatable) {
        return Celestisynth.prefix("animations/mob/star_monolith.animation.json");
    }
}
