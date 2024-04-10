package com.aqutheseal.celestisynth.common.entity.projectile;

import com.aqutheseal.celestisynth.api.item.AttackHurtTypes;
import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class KeresSlash extends ThrowableProjectile implements GeoEntity, CSWeaponUtil {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public final KeresSlashPart[] subEntities;
    public final KeresSlashPart center, left, left1, left2, right, right1, right2;

    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(KeresSlash.class, EntityDataSerializers.FLOAT);

    public KeresSlash(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        center = new KeresSlashPart(this);
        left = new KeresSlashPart(this);
        left1 = new KeresSlashPart(this);
        left2 = new KeresSlashPart(this);
        right = new KeresSlashPart(this);
        right1 = new KeresSlashPart(this);
        right2 = new KeresSlashPart(this);
        subEntities = new KeresSlashPart[]{center, left, left1, left2, right, right1, right2};
    }

    public KeresSlash(EntityType<? extends ThrowableProjectile> pEntityType, LivingEntity pShooter, Level pLevel) {
        super(pEntityType, pShooter, pLevel);
        center = new KeresSlashPart(this);
        left = new KeresSlashPart(this);
        left1 = new KeresSlashPart(this);
        left2 = new KeresSlashPart(this);
        right = new KeresSlashPart(this);
        right1 = new KeresSlashPart(this);
        right2 = new KeresSlashPart(this);
        subEntities = new KeresSlashPart[]{center, left, left1, left2, right, right1, right2};
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 40) {
            this.remove(RemovalReason.DISCARDED);
        }

        Vec3 look = this.getLookAngle().normalize().relative(Direction.UP, 1).scale(0.2);
        float yRadians = -getViewYRot(1.0F) * Mth.DEG_TO_RAD;
        Vec3 viewRad = new Vec3(Mth.cos(yRadians), 0, Mth.sin(yRadians));
        Vec3 view;
        this.center.moveTo(this.position().add(look));
        {
            view = viewRad.scale(0.5);
            this.left.moveTo(this.position().add(look.add(view)));
            this.right.moveTo(this.position().add(look.subtract(view)));
        }
        {
            view = viewRad;
            this.left1.moveTo(this.position().add(look.add(view)));
            this.right1.moveTo(this.position().add(look.subtract(view)));
        }
        {
            view = viewRad.scale(1.5);
            this.left2.moveTo(this.position().add(look.add(view)));
            this.right2.moveTo(this.position().add(look.subtract(view)));
        }

        for (PartEntity<?> part : getParts()) {
            Vec3 particleDir = getDeltaMovement().normalize().reverse().scale(0.2);
            ParticleUtil.sendParticle(level(), CSParticleTypes.KERES_OMEN.get(), part.getX(), part.getY() + 0.25, part.getZ(), particleDir.x(), particleDir.y(), particleDir.z());
            if (this.getOwner() instanceof LivingEntity owner) {
                for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, part.getBoundingBox(), livingEntity -> livingEntity != this.getOwner())) {
                    initiateAbilityAttack(owner, target, 1, AttackHurtTypes.RAPID_NO_KB);
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        for (PartEntity<?> part : getParts()) {
            for (int i = 0; i < 30; i++) {
                ParticleUtil.sendParticle(level(), ParticleTypes.CAMPFIRE_COSY_SMOKE, part.getX(), part.getY() + 0.25, part.getZ(), random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02);
            }
        }
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public PartEntity<?>[] getParts() {
        return subEntities;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, (state) -> {
            //
            return state.setAndContinue(RawAnimation.begin().thenLoop("animation.keres_slash.idle"));
        }));
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

    public float getRoll() {
        return this.entityData.get(ROLL);
    }

    public void setRoll(float roll) {
        this.entityData.set(ROLL, roll);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ROLL, 0F);
    }

    public static class KeresSlashPart extends PartEntity<KeresSlash> {
        public KeresSlashPart(KeresSlash parent) {
            super(parent);
            refreshDimensions();
        }

        @Override
        public EntityDimensions getDimensions(Pose pPose) {
            return EntityDimensions.scalable(0.5F, 0.5F);
        }

        @Override
        protected void defineSynchedData() {
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag compoundTag) {
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
        }
    }

}
