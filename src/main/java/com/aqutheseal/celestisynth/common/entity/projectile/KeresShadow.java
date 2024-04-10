package com.aqutheseal.celestisynth.common.entity.projectile;

import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.registry.CSDamageSources;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeresShadow extends ThrowableProjectile implements CSWeaponUtil {
    private static final EntityDataAccessor<Integer> HOMING_TARGET = SynchedEntityData.defineId(KeresShadow.class, EntityDataSerializers.INT);

    public KeresShadow(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public KeresShadow(EntityType<? extends ThrowableProjectile> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pX, pY, pZ, pLevel);
    }

    public KeresShadow(EntityType<? extends ThrowableProjectile> pEntityType, LivingEntity pShooter, Level pLevel) {
        super(pEntityType, pShooter, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        double xx = random.nextGaussian() * 0.02;
        double yy = random.nextGaussian() * 0.02;
        double zz = random.nextGaussian() * 0.02;
        Vec3 offsetVector = this.getDeltaMovement().normalize().scale(4);
        ParticleUtil.sendParticle(level(), CSParticleTypes.KERES_ASH.get(), getX() + offsetVector.x(), getY() + offsetVector.y() + 1, getZ() + offsetVector.z(), xx, yy, zz);

        if (getHomingTarget() != null) {
            Vec3 positionAmends = getHomingTarget().position().subtract(this.position());
            double posAmendScale = 0.05D * (double) 3;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(positionAmends.normalize().scale(posAmendScale)));

            if (this.getBoundingBox().intersects(this.getHomingTarget().getBoundingBox())) {
                this.onHitEntity(new EntityHitResult(this.getHomingTarget()));
            }
        } else {
            if (tickCount > 20) {
                TargetingConditions selectFreshTarget = TargetingConditions.forCombat().range(32.0D).ignoreLineOfSight().selector(living -> living != this.getOwner());
                List<LivingEntity> targetList = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32), entity -> selectFreshTarget.test(null, entity));
                if (!targetList.isEmpty()) {
                    this.setHomingTarget(targetList.get(random.nextInt(targetList.size())));
                } else {
                    if (this.getOwner() instanceof LivingEntity owner) {
                        this.setHomingTarget(owner);
                    } else {
                        this.remove(RemovalReason.DISCARDED);
                    }
                }
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (pResult.getEntity() instanceof LivingEntity target) {
            if (this.getOwner() instanceof LivingEntity owner) {
                if (target == owner) {
                    owner.heal(1F);
                    if (owner instanceof Player player) {
                        player.getFoodData().eat(2, 0F);
                    }
                    owner.playSound(SoundEvents.AMETHYST_BLOCK_BREAK);
                } else {
                    target.hurt(CSDamageSources.instance(level()).rapidPlayerAttack(owner), 6F);
                    target.playSound(SoundEvents.WITHER_BREAK_BLOCK, 0.2F, 1 + (random.nextFloat() * 0.5F));
                }
                if (target == getHomingTarget()) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
    }

//    @Override
//    protected void onHitBlock(BlockHitResult pResult) {
//        super.onHitBlock(pResult);
//        this.remove(RemovalReason.DISCARDED);
//    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    public @Nullable LivingEntity getHomingTarget() {
        int data = entityData.get(HOMING_TARGET);
        if (data == 0) {
            return null;
        }
        Entity target = level().getEntity(entityData.get(HOMING_TARGET));
        if (target instanceof LivingEntity living) {
            return living;
        } else {
            return null;
        }
    }

    public void setHomingTarget(LivingEntity target) {
        entityData.set(HOMING_TARGET, target.getId());
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(HOMING_TARGET, 0);
    }
}
