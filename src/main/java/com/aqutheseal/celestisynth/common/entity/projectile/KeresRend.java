package com.aqutheseal.celestisynth.common.entity.projectile;

import com.aqutheseal.celestisynth.api.item.AttackHurtTypes;
import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
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

import java.util.List;

public class KeresRend extends ThrowableProjectile implements GeoEntity, CSWeaponUtil {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

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

        this.shakeScreensForNearbyPlayers(this, level(), 6, 60, 60, 0.02F);

        Vec3 particleDelta = this.getDeltaMovement().normalize().scale(0.25).reverse();
        for (double i = 0; i <= 10; i = i + 0.2) {
            ParticleUtil.sendParticle(level(), CSParticleTypes.KERES_OMEN.get(), getX(), this.getY() + i, getZ(), particleDelta.x(), 0, particleDelta.z());
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
                    if (!blockstate.isAir()) {
                        if (!blockstate.is(BlockTags.DRAGON_IMMUNE)) {
                            if (!level().isClientSide) {
                                if (yy == minY) {
                                    this.level().setBlock(blockpos, Fluids.FLOWING_LAVA.defaultFluidState().createLegacyBlock(), 2);
                                } else {
                                    this.level().removeBlock(blockpos, false);
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
        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, pArea).stream().filter(living -> living != this.getOwner()).toList();
        for (LivingEntity target : targets) {
            if (getOwner() instanceof LivingEntity owner) {
                this.initiateAbilityAttack(owner, target, 5F + (target.getHealth() * 0.1F), level().damageSources().magic(), AttackHurtTypes.RAPID_NO_KB);
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
