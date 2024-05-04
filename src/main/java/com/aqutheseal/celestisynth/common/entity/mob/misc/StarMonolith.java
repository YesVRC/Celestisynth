package com.aqutheseal.celestisynth.common.entity.mob.misc;

import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.entity.base.FixedMovesetEntity;
import com.aqutheseal.celestisynth.common.entity.base.MonolithSummonedEntity;
import com.aqutheseal.celestisynth.common.entity.goals.star_monolith.StarMonolithSpikeGoal;
import com.aqutheseal.celestisynth.common.entity.helper.MonolithRunes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class StarMonolith extends Mob implements GeoEntity, FixedMovesetEntity, CSWeaponUtil {
    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.defineId(StarMonolith.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIMATION_TICK = SynchedEntityData.defineId(StarMonolith.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(StarMonolith.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RUNE = SynchedEntityData.defineId(StarMonolith.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final int ACTION_SPIKE = 1;

    public static final int VARIANT_NETHER = 1;


    public StarMonolith(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StarMonolithSpikeGoal(this));
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.getRune().ambientTick.accept(this, this.level());
        if (!this.level().isClientSide()) {
            if (level().getDifficulty() != Difficulty.PEACEFUL && this.hasNearbyAlivePlayerWithFilter(getX(), getY(), getZ(), getRune().summonRange * 1.5)) {
                if (tickCount % getRune().summonInterval == 0) {
                    int summoned = StreamSupport.stream(((ServerLevel) level()).getAllEntities().spliterator(), false).filter(e -> e instanceof MonolithSummonedEntity summonedEntity && summonedEntity.getMonolith() == this).toList().size();
                    if (summoned < getRune().summonLimit) {
                        List<BlockPos> validSpawns = getValidSpawnPoints(getRune().summonRange);
                        this.getRune().summonAction.accept(this, validSpawns.get(random.nextInt(validSpawns.size())), (ServerLevel) this.level());
                    }
                }
            }
        }
    }

    public boolean hasNearbyAlivePlayerWithFilter(double pX, double pY, double pZ, double pDistance) {
        for(Player player : this.level().players()) {
            if (EntitySelector.NO_SPECTATORS.test(player) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(player) && this.hasLineOfSight(player)) {
                double distanceOfPlayer = player.distanceToSqr(pX, pY, pZ);
                if (pDistance < 0.0D || distanceOfPlayer < pDistance * pDistance) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<BlockPos> getValidSpawnPoints(int range) {
        ArrayList<BlockPos> blockPositions = new ArrayList<>();
        for (int xx = -range; xx <= range; xx++) {
            for (int yy = -range; yy <= range; yy++) {
                for (int zz = -range; zz <= range; zz++) {
                    BlockPos toAddPos = this.blockPosition().offset(xx, yy, zz);
                    if (level().getBlockState(toAddPos.below()).isFaceSturdy(level(), toAddPos.below(), Direction.UP)) {
                        if (level().getBlockState(toAddPos).isAir()) {
                            if (hasLineOfSight(toAddPos)) {
                                blockPositions.add(toAddPos);
                            }
                        }
                    }
                }
            }
        }
        return blockPositions;
    }

    public boolean hasLineOfSight(BlockPos pos) {
        Vec3 vec3 = this.position();
        Vec3 vec31 = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        return this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, (state) -> {
            if (getAction() == ACTION_SPIKE) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("animation.star_monolith.spike"));
            }
            if (isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("animation.star_monolith.death"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("animation.star_monolith.idle"));
        }));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.FLYING_SPEED, 0.0);
    }

    public static boolean canSpawn(EntityType<StarMonolith> pEntityType, ServerLevelAccessor pServerLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return checkMobSpawnRules(pEntityType, pServerLevel, pSpawnType, pPos, pRandom);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.moveTo(this.getFloorPositionUnderPlayer(level(), this.blockPosition()).above(), 0, 0);
        this.setRune(MonolithRunes.BLOOD_RUNE);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.BLAZE_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BEACON_DEACTIVATE;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.setActionToDefault();
        if (this.deathTime >= 40 && !this.level().isClientSide() && !this.isRemoved()) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setVariant(pCompound.getInt("variant"));
        this.setRune(MonolithRunes.values()[pCompound.getInt("rune")]);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (pCompound.contains("variant")) {
                pCompound.putInt("variant", this.getVariant());
        }
        if (pCompound.contains("rune")) {
            pCompound.putInt("rune", this.getRune().ordinal());
        }
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setRune(MonolithRunes rune) {
        this.entityData.set(RUNE, rune.ordinal());
    }

    public MonolithRunes getRune() {
        return MonolithRunes.values()[this.entityData.get(RUNE)];
    }

    @Override
    public void setAction(int action) {
        this.entityData.set(ACTION, action);
    }

    @Override
    public int getAction() {
        return this.entityData.get(ACTION);
    }

    @Override
    public int getAnimationTick() {
        return this.entityData.get(ANIMATION_TICK);
    }

    @Override
    public void setAnimationTick(int tick) {
        this.entityData.set(ANIMATION_TICK, tick);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTION, 0);
        this.entityData.define(ANIMATION_TICK, 0);
        this.entityData.define(RUNE, 0);
        this.entityData.define(VARIANT, 0);
    }

    public void setDeltaMovement(Vec3 motionIn) {
        super.setDeltaMovement(Vec3.ZERO.add(0, -0.3, 0));
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

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
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
}
