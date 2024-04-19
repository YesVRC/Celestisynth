package com.aqutheseal.celestisynth.common.mobeffect;

import com.aqutheseal.celestisynth.common.registry.CSMobEffects;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CSMobEffect extends MobEffect {

    public CSMobEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        super.applyEffectTick(pLivingEntity, pAmplifier);
        if (this == CSMobEffects.CURSEBANE.get()) {
            for (int i = 0; i < 36; i++) {
                int j = i * 10;
                Vec3 particleVector = new Vec3(Mth.sin(j) * 3, 0.5, Mth.cos(j) * 3);
                ParticleUtil.sendParticle(pLivingEntity.level(), CSParticleTypes.KERES_ASH.get(), pLivingEntity.position(), particleVector);
            }
        }
    }
}
