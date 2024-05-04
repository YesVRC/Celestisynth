package com.aqutheseal.celestisynth.common.entity.mob.misc;

import com.aqutheseal.celestisynth.common.entity.base.CSEffectEntity;
import com.aqutheseal.celestisynth.common.entity.base.SummonableEntity;
import com.aqutheseal.celestisynth.common.entity.goals.CSLookAroundGoal;
import com.aqutheseal.celestisynth.common.entity.goals.CSLookAtTargetGoal;
import com.aqutheseal.celestisynth.common.entity.goals.CSOwnerAttackGoal;
import com.aqutheseal.celestisynth.common.entity.goals.CSOwnerAttackedGoal;
import com.aqutheseal.celestisynth.common.entity.projectile.RainfallArrow;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import com.aqutheseal.celestisynth.common.registry.CSVisualTypes;
import com.aqutheseal.celestisynth.manager.CSConfigManager;
import com.aqutheseal.celestisynth.util.TargetUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RainfallTurret extends SummonableEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(RainfallTurret.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> X_SYNC_ROT = SynchedEntityData.defineId(RainfallTurret.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int shootTime;

    public RainfallTurret(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            //System.out.println("XRot: " + this.getXRot());
            this.setXSyncedRot(this.getXRot());
        }
        this.setYBodyRot(0);
        this.yBodyRotO = 0;
        this.tickShooting();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CSLookAtTargetGoal(this));
        this.goalSelector.addGoal(3, new CSLookAroundGoal(this, entity -> entity.getTarget() == null));
        this.targetSelector.addGoal(3, new CSOwnerAttackedGoal<>(this));
        this.targetSelector.addGoal(4, new CSOwnerAttackGoal<>(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, target -> TargetUtil.isValidTargetForOwnable(this, target)));
        super.registerGoals();
    }

    public void tickShooting() {
        int shootInterval = 10;
        if (this.getTarget() != null && !this.isRemoved() && this.getOwner() instanceof Player player) {
            this.shootTime++;
            if (this.shootTime == shootInterval) {
                this.entityData.set(SHOOTING, true);
                this.playSound(CSSoundEvents.LASER_SHOOT.get(), 0.2F, 0.2F + random.nextFloat());
                CSEffectEntity.createInstance(player, this, CSVisualTypes.RAINFALL_SHOOT.get(), this.getLookAngle().x() * 2, 0.5 + this.getLookAngle().y() * 2, this.getLookAngle().z() * 2);
                double finalDistX = this.getTarget().getX() - this.getX();
                double finalDistY = this.getTarget().getY() - this.getY();
                double finalDistZ = this.getTarget().getZ() - this.getZ();
                if (!level().isClientSide) {
                    RainfallArrow rainfallArrow = new RainfallArrow(level(), player);
                    rainfallArrow.setOwner(player);
                    rainfallArrow.moveTo(this.position().add(0, 0.5, 0));
                    rainfallArrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    rainfallArrow.setOrigin(rainfallArrow.position().add(0, 0.5, 0));
                    rainfallArrow.setPierceLevel((byte) 3);
                    rainfallArrow.setBaseDamage(CSConfigManager.COMMON.rainfallSerenityQuasarArrowDmg.get());
                    rainfallArrow.setImbueQuasar(true);
                    rainfallArrow.shoot(finalDistX, finalDistY, finalDistZ, 1.0F, 0);
                    level().addFreshEntity(rainfallArrow);
                }
            }
            if (this.shootTime > shootInterval + 20) {
                this.entityData.set(SHOOTING, false);
                this.shootTime = 5 - random.nextInt(5);
            }
        } else {
            this.entityData.set(SHOOTING, false);
            this.shootTime = 0;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOTING, false);
        this.entityData.define(X_SYNC_ROT, 0F);
    }

    public void setXSyncedRot(float value) {
        this.entityData.set(X_SYNC_ROT, value);
    }

    public float getXSyncedRot() {
        return this.entityData.get(X_SYNC_ROT);
    }

    @Override
    protected void tickDeath() {
        this.destroy();
    }

    public void destroy() {
        if (!level().isClientSide) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(), this.getDeathSound(), this.getSoundSource(), 1.0F, 2.0F);
            ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ANDESITE.defaultBlockState()), this.getX(), this.getY(), this.getZ(),
                    50, this.getBbWidth(), this.getBbHeight(), this.getBbWidth(), 0.1
            );
            this.remove(RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!level().isClientSide) {
            ((ServerLevel) level()).sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ANDESITE.defaultBlockState()), this.getX(), this.getY(), this.getZ(),
                    25, this.getBbWidth(), this.getBbHeight(), this.getBbWidth(), 0.05
            );
        }
        if (pSource.getEntity() instanceof Player entity) {
            this.setOwner(entity);
        }
        return super.hurt(pSource, pAmount);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, (state) -> {
            if (this.entityData.get(SHOOTING)) {
                //state.setControllerSpeed(0.5f);
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.rainfall_turret.shoot"));
            }
            state.setControllerSpeed(1);
            return state.setAndContinue(RawAnimation.begin().thenLoop("animation.rainfall_turret.idle"));
        }));
    }

    public boolean isControlledByLocalInstance() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.FLYING_SPEED, 0.0);
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == this.damageSources().drown() || source == this.damageSources().inWall();
    }

    protected Vec3 getLeashOffset() {
        return new Vec3(0.0, this.getEyeHeight() - 1.0F, 0.0);
    }

    public void setDeltaMovement(Vec3 motionIn) {
    }

    public void knockback(double strength, double x, double z) {
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isPushable() {
        return false;
    }

    protected void markHurt() {
    }

    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.GILDED_BLACKSTONE_BREAK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BONE_BLOCK_BREAK;
    }
}
