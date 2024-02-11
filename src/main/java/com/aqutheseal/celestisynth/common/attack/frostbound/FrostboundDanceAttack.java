package com.aqutheseal.celestisynth.common.attack.frostbound;

import com.aqutheseal.celestisynth.api.animation.player.AnimationManager;
import com.aqutheseal.celestisynth.api.item.AttackHurtTypes;
import com.aqutheseal.celestisynth.common.attack.base.WeaponAttackInstance;
import com.aqutheseal.celestisynth.common.capabilities.CSEntityCapabilityProvider;
import com.aqutheseal.celestisynth.common.entity.base.CSEffectEntity;
import com.aqutheseal.celestisynth.common.entity.helper.CSVisualType;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import com.aqutheseal.celestisynth.common.registry.CSVisualTypes;
import com.aqutheseal.celestisynth.manager.CSConfigManager;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FrostboundDanceAttack extends WeaponAttackInstance {
    public FrostboundDanceAttack(Player player, ItemStack stack) {
        super(player, stack);
    }

    @Override
    public AnimationManager.AnimationsList getAnimation() {
        return AnimationManager.AnimationsList.ANIM_FROSTBOUND_TRIPLE_SLASH;
    }

    @Override
    public int getCooldown() {
        return CSConfigManager.COMMON.frostboundSkillCD.get();
    }

    @Override
    public int getAttackStopTime() {
        return 50;
    }

    @Override
    public boolean getCondition() {
        return !player.isShiftKeyDown();
    }

    @Override
    public void startUsing() {
        player.setDeltaMovement(0, 0.75, 0);
        player.playSound(CSSoundEvents.HOP.get());
    }

    @Override
    public void tickAttack() {
        double xP = calculateXLook(player) * 1.5;
        double zP = calculateZLook(player) * 1.5;

        if (getTimerProgress() == 12) {
            BlockPos groundPos = this.getFloorPositionUnderPlayer(level, player.blockPosition());
            player.setDeltaMovement(0, (groundPos.getY() - player.getY()), 0);
            this.playSoundAt(level, SoundEvents.GLASS_BREAK, groundPos);
            this.playSoundAt(level, CSSoundEvents.SWORD_CLASH.get(), groundPos);
            CSEffectEntity.createInstanceLockedPos(player, null, CSVisualTypes.FROSTBOUND_IMPACT_CRACK.get(), player.getX() + xP, groundPos.getY() - 0.75, player.getZ() + zP);
            for (int i = 0; i < 360; i++) {
                double xI = xP + Mth.sin(i) * 3;
                double zI = zP + Mth.cos(i) * 3;
                ParticleUtil.sendParticles(level, ParticleTypes.SNOWFLAKE, player.getX() + xI, groundPos.getY() + 1.5, player.getZ() + zI, 1, (xI / 5), 0, (zI / 5));
            }
            for (Entity entity : iterateEntities(level, createAABB(groundPos.offset((int) xP, 1, (int) zP), 6, 3))) {
                if (entity instanceof LivingEntity target && entity != player) {
                    initiateAbilityAttack(player, target, (float) (double) CSConfigManager.COMMON.frostboundSkillDmg.get() + getSharpnessValue(stack, 1.75F), AttackHurtTypes.NO_KB);
                    entity.getCapability(CSEntityCapabilityProvider.CAPABILITY).ifPresent(data -> {
                        data.setFrostbound(100);
                    });
                    entity.playSound(SoundEvents.PLAYER_HURT_FREEZE);
                }
            }
        }
        if (getTimerProgress() == 20) {
            this.playSoundAt(level, CSSoundEvents.FROZEN_SLASH.get(), player.blockPosition().offset((int) xP, 0, (int) zP));
            doAOEAttack(CSVisualTypes.FROSTBOUND_SLASH.get(), xP * 2.5, zP * 2.5, false);
            player.setDeltaMovement(calculateXLook(player) * 1.5 , 0.25, calculateZLook(player) * 1.5);
        } else if (getTimerProgress() == 30) {
            this.playSoundAt(level, CSSoundEvents.FROZEN_SLASH.get(), player.blockPosition().offset((int) xP, 0, (int) zP));
            doAOEAttack(CSVisualTypes.FROSTBOUND_SLASH_INVERTED.get(), xP * 2.5, zP * 2.5, false);
            player.setDeltaMovement(calculateXLook(player) * 1.5 , 0.25, calculateZLook(player) * 1.5);
        } else if (getTimerProgress() == 40) {
            this.playSoundAt(level, CSSoundEvents.FROZEN_SLASH.get(), player.blockPosition().offset((int) xP, 0, (int) zP));
            doAOEAttack(CSVisualTypes.FROSTBOUND_SLASH_LARGE.get(), xP * 2.5, zP * 2.5, true);
            player.setDeltaMovement(calculateXLook(player) * 1.5 , 0.25, calculateZLook(player) * 1.5);
        }
    }

    public void doAOEAttack(CSVisualType visual, double xP, double zP, boolean isLarge) {
        CSEffectEntity.createInstance(player, player, visual, xP, isLarge ? 0.15 : 0.25, zP);
        for (int i = 0; i < 360; i++) {
            double sizeMult = isLarge ? 3 : 1;
            double xI = xP + Mth.sin(i) * 3;
            double zI = zP + Mth.cos(i) * 3;
            ParticleUtil.sendParticles(level, ParticleTypes.SNOWFLAKE, player.getX() + xI, player.getY() + 0.5, player.getZ() + zI, 1, (xI / 10) * sizeMult, 0, (zI / 10) * sizeMult);
        }
        for (Entity entity : iterateEntities(level, createAABB(player.blockPosition().offset((int) xP, 1, (int) zP), isLarge ? 8 : 5, 3))) {
            if (entity instanceof LivingEntity target && entity != player) {
                initiateAbilityAttack(player, target, 6 + getSharpnessValue(stack, 1.5F), AttackHurtTypes.REGULAR);
                entity.getCapability(CSEntityCapabilityProvider.CAPABILITY).ifPresent(data -> {
                    data.setFrostbound(60);
                });
                entity.playSound(SoundEvents.PLAYER_HURT_FREEZE);
            }
        }
    }

    @Override
    public void stopUsing() {
    }
}
