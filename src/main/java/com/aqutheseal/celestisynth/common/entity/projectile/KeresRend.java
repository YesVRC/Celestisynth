package com.aqutheseal.celestisynth.common.entity.projectile;

import com.aqutheseal.celestisynth.api.item.AttackHurtTypes;
import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.registry.CSDamageSources;
import com.aqutheseal.celestisynth.common.registry.CSMobEffects;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.mixin.LivingEntityInvoker;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
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
        this.setDeltaMovement(this.getDeltaMovement().scale(1.1));

        AABB movedBB = this.getBoundingBox().inflate(0, 16, 0).move(0, 12, 0);
        for (double i = 0; i <= 15; i += 3) {
            this.checkWalls(movedBB.move(getDeltaMovement().normalize().scale(i)));
        }

        this.shakeScreensForNearbyPlayers(this, level(), 128, 40, 30, 0.04F);

        for (int i = 0; i < 45; i++) {
            Vec3 particleDir = Vec3.ZERO.add(Mth.sin(i), Mth.cos(i), 0).scale(1.4);
            Vec3 rotated = particleDir.xRot(this.getXRot() * Mth.DEG_TO_RAD).yRot(this.getYRot() * Mth.DEG_TO_RAD);
            ParticleUtil.sendParticle(level(), CSParticleTypes.KERES_OMEN.get(), this.position().add(0, 3, 0).add(this.getLookAngle().scale(2)), rotated);
        }

        Vec3 deltaBase = this.getDeltaMovement().normalize();
        for (double i = 0; i <= 45; i++) {
            Vec3 rotRight = deltaBase.scale(random.nextGaussian() * 0.75).yRot(90);
            ParticleUtil.sendParticle(level(), ParticleTypes.POOF, getX(), this.getY() + 4, getZ(), rotRight.x(), random.nextGaussian() * 0.75, rotRight.z());
            Vec3 rotLeft = deltaBase.scale(random.nextGaussian() * 0.75).yRot(-90);
            ParticleUtil.sendParticle(level(), ParticleTypes.POOF, getX(), this.getY() + 4, getZ(), rotLeft.x(), random.nextGaussian() * 0.75, rotLeft.z());
        }

        if (tickCount >= 30) {
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
        double iter = 0;
        int minY = Mth.floor(pArea.minY);
        int maxY = Mth.floor(pArea.maxY);
        for (int yy = minY; yy <= maxY; ++yy) {
            iter = iter + 0.25F;
            pArea = pArea.inflate(iter / 6);
            int minX = Mth.floor(pArea.minX);
            int maxX = Mth.floor(pArea.maxX);
            int minZ = Mth.floor(pArea.minZ);
            int maxZ = Mth.floor(pArea.maxZ);
            for (int xx = minX; xx <= maxX; ++xx) {
                for (int zz = minZ; zz <= maxZ; ++zz) {
                    BlockPos blockpos = new BlockPos(xx, yy, zz);
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    if (!blockstate.isAir() && !blockstate.liquid()) {
                        if (!blockstate.is(BlockTags.DRAGON_IMMUNE)) {
                            if (!(blockpos.getX() == getOwner().getBlockX() && blockpos.getZ() == getOwner().getBlockZ())) {
                                if (!level().isClientSide) {
                                    if (yy == minY) {
                                        this.level().setBlock(blockpos, Fluids.LAVA.defaultFluidState().createLegacyBlock(), 2);
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
        }
        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, pArea).stream().filter(living -> living != this.getOwner() && !finishedAttacking.contains(living)).toList();
        for (LivingEntity target : targets) {
            if (getOwner() instanceof LivingEntity owner) {
                target.addEffect(new MobEffectInstance(CSMobEffects.CURSEBANE.get(), 100, 1));

                float damageCalculation = 15F + (target.getMaxHealth() * 0.25F);
                if (ForgeRegistries.ENTITY_TYPES.getKey(target.getType()).getNamespace().equals("cataclysm")) {
                    this.bypassAllHurt(target, owner, damageCalculation);
                } else {
                    this.initiateAbilityAttack(owner, target, damageCalculation, CSDamageSources.instance(level()).erasure(owner), AttackHurtTypes.RAPID_NO_KB);
                    if (target.isDeadOrDying()) {
                        target.remove(Entity.RemovalReason.KILLED);
                    }
                }

                owner.heal(5F);
                finishedAttacking.add(target);
            }
        }
    }

    public boolean bypassAllHurt(LivingEntity target, LivingEntity owner, float pAmount) {
        DamageSource pSource = CSDamageSources.instance(level()).erasure(owner);
        if (!net.minecraftforge.common.ForgeHooks.onLivingAttack(target, pSource, pAmount)) return false;
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else if (target.level().isClientSide) {
            return false;
        } else if (target.isDeadOrDying()) {
            return false;
        } else {
            if (target.isSleeping() && !this.level().isClientSide) {
                target.stopSleeping();
            }
            target.setNoActionTime(0);
            float f = pAmount;
            boolean flag = false;
            target.walkAnimation.setSpeed(1.5F);
            //target.lastHurt = pAmount;
            target.invulnerableTime = 20;
            ((LivingEntityInvoker) target).invokeActuallyHurt(pSource, pAmount);
            target.hurtDuration = 10;
            target.hurtTime = target.hurtDuration;
            Entity entity1 = pSource.getEntity();
            if (entity1 != null) {
                if (entity1 instanceof LivingEntity) {
                    LivingEntity livingentity1 = (LivingEntity) entity1;
                    if (!pSource.is(DamageTypeTags.NO_ANGER)) {
                        target.setLastHurtByMob(livingentity1);
                    }
                }
                if (entity1 instanceof Player) {
                    Player player1 = (Player) entity1;
                    target.lastHurtByPlayerTime = 100;
                    target.setLastHurtByPlayer(player1);
                } else if (entity1 instanceof net.minecraft.world.entity.TamableAnimal tamableEntity) {
                    if (tamableEntity.isTame()) {
                        target.lastHurtByPlayerTime = 100;
                        LivingEntity livingentity2 = tamableEntity.getOwner();
                        if (livingentity2 instanceof Player) {
                            Player player = (Player) livingentity2;
                            target.setLastHurtByPlayer(player);
                        } else {
                            target.setLastHurtByPlayer(null);
                        }
                    }
                }
            }
            target.level().broadcastDamageEvent(this, pSource);
            target.hurtMarked = true;
            boolean flag2 = true;
            target.lastDamageSource = pSource;
            target.lastDamageStamp = this.level().getGameTime();

            if (target instanceof ServerPlayer) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer) target, pSource, f, pAmount, flag);
            }

            if (entity1 instanceof ServerPlayer) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)entity1, this, pSource, f, pAmount, flag);
            }

            if (target.isDeadOrDying()) {
                target.remove(Entity.RemovalReason.KILLED);
            }

            return flag2;
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
