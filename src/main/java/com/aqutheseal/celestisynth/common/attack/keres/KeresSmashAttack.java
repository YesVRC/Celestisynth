package com.aqutheseal.celestisynth.common.attack.keres;

import com.aqutheseal.celestisynth.api.animation.player.PlayerAnimationContainer;
import com.aqutheseal.celestisynth.common.attack.base.WeaponAttackInstance;
import com.aqutheseal.celestisynth.common.entity.skill.SkillCastKeresSmash;
import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSMobEffects;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.common.registry.CSPlayerAnimations;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class KeresSmashAttack extends WeaponAttackInstance {
    public KeresSmashAttack(Player player, ItemStack stack, int heldDuration) {
        super(player, stack, heldDuration);
    }

    @Override
    public PlayerAnimationContainer getAnimation() {
        return CSPlayerAnimations.ANIM_KERES_SMASH.get();
    }

    @Override
    public boolean sameAnimationForBothHands() {
        return true;
    }

    @Override
    public int getCooldown() {
        return 100;
    }

    @Override
    public int getAttackStopTime() {
        return 100;
    }

    @Override
    public boolean getCondition() {
        return player.isShiftKeyDown() && heldDuration < 200;
    }

    @Override
    public void startUsing() {
        this.getTagExtras().putFloat("cs.keres.xx", (float) (calculateXLook(player) * ((double) heldDuration / 3)));
        this.getTagExtras().putFloat("cs.keres.zz", (float) (calculateZLook(player) * ((double) heldDuration / 3)));
        float xx = this.getTagExtras().getFloat("cs.keres.xx");
        float zz = this.getTagExtras().getFloat("cs.keres.zz");
        Vec3 targetPos = (player.position().add(xx, 0, zz));
        player.setDeltaMovement(0, 1 + (player.position().distanceTo(targetPos) * 0.001), 0);
        player.setOnGround(false);
    }

    @Override
    public void tickAttack() {
        if (!player.onGround()) {
            float xx = this.getTagExtras().getFloat("cs.keres.xx");
            float zz = this.getTagExtras().getFloat("cs.keres.zz");
            Vec3 targetPos = (player.position().add(xx, 0, zz));
            Vec3 targetDelta = targetPos.subtract(player.position()).scale(0.005);
            player.setDeltaMovement(player.getDeltaMovement().add(targetDelta.x, 0, targetDelta.z));
            for (int i = 0; i < 15; i++) {
                ParticleUtil.sendParticle(level, CSParticleTypes.KERES_ASH.get(), player.getX(), player.getY(), player.getZ(), level.random.nextGaussian() * 0.1, level.random.nextGaussian() * 0.1,  level.random.nextGaussian() * 0.1);
            }
            player.fallDistance = 0;
        } else {
            player.setDeltaMovement(0, 0, 0);
            player.addEffect(new MobEffectInstance(CSMobEffects.HELLBANE.get(), 60, 4));
            if (!level.isClientSide()) {
                SkillCastKeresSmash smash = CSEntityTypes.KERES_SMASH.get().create(level);
                smash.setOwnerUuid(player.getUUID());
                smash.moveTo(player.position());
                level.addFreshEntity(smash);
            }
            this.baseStop();
        }
    }

    @Override
    public void stopUsing() {
    }
}
