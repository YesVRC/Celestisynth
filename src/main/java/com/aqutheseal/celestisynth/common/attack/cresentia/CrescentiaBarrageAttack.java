package com.aqutheseal.celestisynth.common.attack.cresentia;

import com.aqutheseal.celestisynth.api.animation.player.PlayerAnimationContainer;
import com.aqutheseal.celestisynth.api.item.AttackHurtTypes;
import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.attack.base.WeaponAttackInstance;
import com.aqutheseal.celestisynth.common.entity.base.CSEffectEntity;
import com.aqutheseal.celestisynth.common.entity.projectile.CrescentiaDragon;
import com.aqutheseal.celestisynth.common.item.weapons.CrescentiaItem;
import com.aqutheseal.celestisynth.common.registry.CSPlayerAnimations;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import com.aqutheseal.celestisynth.common.registry.CSVisualTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.object.Color;

import java.util.List;

public class CrescentiaBarrageAttack extends WeaponAttackInstance {

    public CrescentiaBarrageAttack(Player player, ItemStack stack) {
        super(player, stack);
    }

    @Override
    public PlayerAnimationContainer getAnimation() {
        return CSPlayerAnimations.ANIM_CRESCENTIA_STRIKE.get();
    }

    @Override
    public int getCooldown() {
        return 100;
    }

    @Override
    public int getAttackStopTime() {
        return 70;
    }

    @Override
    public boolean getCondition() {
        return !player.isShiftKeyDown();
    }

    @Override
    public void startUsing() {
        useAndDamageItem(getStack(), level, player, 4);
        this.chantMessage(player, "crescentia", 30, Color.WHITE.argbInt());
    }

    @Override
    public void tickAttack() {
        List<CrescentiaDragon> dragons = level.getEntitiesOfClass(CrescentiaDragon.class, player.getBoundingBox().inflate(128)).stream().filter(e -> e.getOwner() == player).toList();
        for (CrescentiaDragon dragon : dragons) {
            Vec3 opp = player.position().add(0, 1, 0).subtract(dragon.position()).normalize().scale(0.25);
            dragon.push(opp.x(), opp.y(), opp.z());
        }

        if (getTimerProgress() == 15) {
            player.playSound(CSSoundEvents.WHIRLWIND.get(), 0.35F, 0.5F + level.random.nextFloat());
            this.chantMessage(player, "crescentia1", 20, Color.MAGENTA.argbInt());
        }

        if (getTimerProgress() >= 15 && getTimerProgress() <= 60) {
            double range = 6.0;
            List<Entity> entities = level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(range, range, range).move(calculateXLook(player), 0, calculateZLook(player)));
            for (Entity entityBatch : entities) {
                if (entityBatch instanceof LivingEntity target) {
                    if (target != player && target.isAlive() && !player.isAlliedTo(target) && target.distanceToSqr(player) <= range * range) {
                        this.attributeDependentAttack(player, target, stack, 0.07F, AttackHurtTypes.RAPID);
                        target.addEffect(CSWeaponUtil.nonVisiblePotionEffect(MobEffects.MOVEMENT_SLOWDOWN, 20, 2));
                    }
                }
                if (entityBatch instanceof Projectile projectile) {
                    CrescentiaItem.createCrescentiaFirework(getStack(), level, player, projectile.getX(), projectile.getY(), projectile.getZ(), true);
                    player.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1.0F, 1.0F);
                    projectile.remove(Entity.RemovalReason.DISCARDED);
                }
            }
            if (getTimerProgress() % 30 == 0) {
                player.playSound(CSSoundEvents.WHIRLWIND.get(), 0.15F, 1F + (float) (player.getRandom().nextGaussian() * 0.25));
            }
            if (getTimerProgress() % 5 == 0) {
                if (level.random.nextBoolean()) {
                    CSEffectEntity.createInstance(player, null, CSVisualTypes.CRESCENTIA_STRIKE.get(), calculateXLook(player), -0.3, calculateZLook(player));
                } else {
                    CSEffectEntity.createInstance(player, null, CSVisualTypes.CRESCENTIA_STRIKE_INVERTED.get(), calculateXLook(player), -0.3, calculateZLook(player));
                }
                playRandomBladeSound(player, BASE_WEAPON_EFFECTS.length);
            }
            if (getTimerProgress() % 2 == 0) {
                CSEffectEntity.createInstance(player, null, CSVisualTypes.SOLARIS_AIR_LARGE.get(), 0, -1, 0);
                float offX = (level.random.nextFloat() * 16) - 8;
                float offY = (level.random.nextFloat() * 16) - 8;
                float offZ = (level.random.nextFloat() * 16) - 8;
                CrescentiaItem.createCrescentiaFirework(getStack(), level, player, player.getX() + offX, player.getY() + offY, player.getZ() + offZ, false);
                if (level.random.nextBoolean()) {
                    CrescentiaItem.createCrescentiaFirework(getStack(), level, player, player.getX() + offZ, player.getY() + offX, player.getZ() + offY, false);
                }
            }
        }
    }

    @Override
    public void stopUsing() {
    }
}
