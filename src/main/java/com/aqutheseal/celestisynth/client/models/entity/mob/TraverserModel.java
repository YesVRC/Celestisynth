package com.aqutheseal.celestisynth.client.models.entity.mob;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.common.entity.mob.natural.Traverser;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TraverserModel extends GeoModel<Traverser> {

    @Override
    public ResourceLocation getModelResource(Traverser animatable) {
        return Celestisynth.prefix("geo/mob/traverser.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Traverser animatable) {
        return Celestisynth.prefix("textures/entity/mob/traverser.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Traverser animatable) {
        return Celestisynth.prefix("animations/mob/traverser.animation.json");
    }
}
