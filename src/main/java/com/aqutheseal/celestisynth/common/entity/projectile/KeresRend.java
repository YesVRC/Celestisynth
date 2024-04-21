package com.aqutheseal.celestisynth.common.entity.projectile;

import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.registry.CSDamageSources;
import com.aqutheseal.celestisynth.common.registry.CSMobEffects;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.mixin.LivingEntityInvoker;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class KeresRend extends ThrowableProjectile implements GeoEntity, CSWeaponUtil {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final List<LivingEntity> finishedAttacking = new ArrayList<>();

    public KeresRend(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public KeresRend(EntityType<? extends ThrowableProjectile> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pX, pY, pZ, pLevel);
    }

    public KeresRend(EntityType<? extends ThrowableProjectile> pEntityType, LivingEntity pShooter, Level pLevel) {
        super(pEntityType, pShooter, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        for (double i = 0; i <= 18; i = i + 3) {
            this.checkWalls(this.getBoundingBox().move(getDeltaMovement().normalize().scale(i)));
        }

        this.shakeScreensForNearbyPlayers(this, level(), 6, 40, 30, 0.04F);

        Vec3 particleDelta = this.getDeltaMovement().normalize().scale(0.25).reverse();
        for (double i = 0; i <= 10; i = i + 0.2) {
            ParticleUtil.sendParticle(level(), CSParticleTypes.KERES_OMEN.get(), getX(), this.getY() + i, getZ(), particleDelta.x(), 0, particleDelta.z());
        }

        Vec3 deltaBase = this.getDeltaMovement().normalize();
        for (double i = 0; i <= 45; i++) {
            Vec3 rotRight = deltaBase.scale(random.nextGaussian() * 0.75).yRot(90);
            ParticleUtil.sendParticle(level(), ParticleTypes.POOF, getX(), this.getY() + 4, getZ(), rotRight.x(), random.nextGaussian() * 0.75, rotRight.z());
            Vec3 rotLeft = deltaBase.scale(random.nextGaussian() * 0.75).yRot(-90);
            ParticleUtil.sendParticle(level(), ParticleTypes.POOF, getX(), this.getY() + 4, getZ(), rotLeft.x(), random.nextGaussian() * 0.75, rotLeft.z());
        }

        if (tickCount >= 15) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, (state) -> {
            //
            return state.setAndContinue(RawAnimation.begin().thenLoop("animation.keres_rend.idle"));
        }));
    }

    private void checkWalls(AABB pArea) {
        int minX = Mth.floor(pArea.minX);
        int minY = Mth.floor(pArea.minY);
        int minZ = Mth.floor(pArea.minZ);
        int maxX = Mth.floor(pArea.maxX);
        int maxY = Mth.floor(pArea.maxY);
        int maxZ = Mth.floor(pArea.maxZ);
        for (int xx = minX; xx <= maxX; ++xx) {
            for (int yy = minY; yy <= maxY; ++yy) {
                for (int zz = minZ; zz <= maxZ; ++zz) {
                    BlockPos blockpos = new BlockPos(xx, yy, zz);
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    if (!blockstate.isAir() && !(blockpos.getX() == getOwner().getBlockX() && blockpos.getZ() == getOwner().getBlockZ())) {
                        if (!blockstate.is(BlockTags.DRAGON_IMMUNE)) {
                            if (!level().isClientSide) {
                                if (yy == minY) {
                                    this.level().setBlock(blockpos, Fluids.FLOWING_LAVA.defaultFluidState().createLegacyBlock(), 2);
                                } else {
                                    this.level().setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
                                }
                            }
                            double xR = random.nextGaussian() * 0.5;
                            double yR = random.nextGaussian() * 0.5;
                            double zR = random.nextGaussian() * 0.5;
                            ParticleUtil.sendParticle(level(), ParticleTypes.FLASH, xx + xR, yy + yR, zz + zR);
                            this.playSound(SoundEvents.BLAZE_SHOOT, 0.1F, 1.0F);
                        }
                    }
                }
            }
        }
        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, pArea).stream().filter(living -> living != this.getOwner() && !finishedAttacking.contains(living)).toList();
        for (LivingEntity target : targets) {
            if (getOwner() instanceof LivingEntity owner) {
                target.addEffect(new MobEffectInstance(CSMobEffects.CURSEBANE.get(), 100, 1));
                this.bypassAllHurt(target, owner, 15F + (target.getMaxHealth() * 0.25F));
                owner.heal(5F);
                finishedAttacking.add(target);
            }
        }
    }

    public void bypassAllHurt(LivingEntity target, LivingEntity owner, float amount) {
        DamageSource erasure = CSDamageSources.instance(level()).erasure(owner);
        ((LivingEntityInvoker) target).invokeActuallyHurt(erasure, amount);
        target.setLastHurtByMob(owner);
        if (owner instanceof Player player) {
            target.setLastHurtByPlayer(player);
            target.lastHurtByPlayerTime = 200;
        }
        target.lastDamageSource = erasure;
        target.lastDamageStamp = this.level().getGameTime();
        if (target instanceof Mob mobTarget) {
            if (mobTarget.getTarget() == null) {
                mobTarget.setTarget(owner);
            }
        }
        this.level().broadcastDamageEvent(target, erasure);
        target.hurtMarked = true;
        if (target.isDeadOrDying()) {
            target.die(erasure);
            if (!target.level().isClientSide()) {
                target.level().broadcastEntityEvent(target, (byte) 60);
                target.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
    }
}
