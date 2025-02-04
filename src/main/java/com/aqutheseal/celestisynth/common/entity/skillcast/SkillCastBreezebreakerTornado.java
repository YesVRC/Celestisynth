package com.aqutheseal.celestisynth.common.entity.skillcast;

import com.aqutheseal.celestisynth.api.item.AttackHurtTypes;
import com.aqutheseal.celestisynth.common.entity.base.CSEffectEntity;
import com.aqutheseal.celestisynth.common.entity.base.EffectControllerEntity;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import com.aqutheseal.celestisynth.common.registry.CSVisualTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public class SkillCastBreezebreakerTornado extends EffectControllerEntity {

    public SkillCastBreezebreakerTornado(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        UUID ownerUuid = getOwnerUUID();
        Player ownerPlayer = ownerUuid == null ? null : this.level().getPlayerByUUID(ownerUuid);
        setAngleX(getAngleX() + getAddAngleX());
        setAngleY(getAngleY() + getAddAngleY());
        setAngleZ(getAngleZ() + getAddAngleZ());
        double newX = getX() + getAngleX();
        double newY = getY() + getAngleY();
        double newZ = getZ() + getAngleZ();
        BlockPos newPos = new BlockPos((int) newX, (int) newY, (int) newZ);

        double range = 6.0;
        List<Entity> entities = level().getEntitiesOfClass(Entity.class, new AABB(newX + range, newY + (range * 2), newZ + range, newX - range, newY - range, newZ - range));

        for (Entity entityBatch : entities) {
            if (entityBatch instanceof LivingEntity target) {
                if (target != ownerPlayer && target.isAlive()) {
                    this.initiateAbilityAttack(ownerPlayer, target, damage, AttackHurtTypes.RAPID_NO_KB);
                    target.setDeltaMovement(0, 0.05, 0);
                }
            }
            if (entityBatch instanceof Projectile projectile) projectile.remove(RemovalReason.DISCARDED);
        }

        for (int yLevel = -1; yLevel < 6; yLevel++) {
            if (yLevel == -1 || yLevel == 0 || yLevel == 1) CSEffectEntity.createInstance(ownerPlayer, this, CSVisualTypes.SOLARIS_AIR_FLAT.get(), getAngleX(), getAngleY() + yLevel, getAngleZ());
            if (yLevel == 2 || yLevel == 3) CSEffectEntity.createInstance(ownerPlayer, this, CSVisualTypes.SOLARIS_AIR_MEDIUM_FLAT.get(), getAngleX(), getAngleY() + yLevel, getAngleZ());
            if (yLevel == 4 || yLevel == 5) CSEffectEntity.createInstance(ownerPlayer, this, CSVisualTypes.SOLARIS_AIR_LARGE_FLAT.get(), getAngleX(), getAngleY() + yLevel, getAngleZ());
        }

        if (tickCount % 20 == 0) level().playSound(level().getPlayerByUUID(getOwnerUUID()), getAngleX(), getAngleY(), getAngleZ(), CSSoundEvents.WHIRLWIND.get(), SoundSource.HOSTILE, 0.10F, 0.5F + random.nextFloat());

        int radius = 2;
        for (int sx = -radius; sx <= radius; sx++) {
            for (int sy = -radius; sy <= radius; sy++) {
                for (int sz = -radius; sz <= radius; sz++) {
                    if (level().getBlockState(newPos.offset(sx, sy, sz)).canBeReplaced()) level().destroyBlock(newPos.offset(sx, sy, sz), false, ownerPlayer);
                }
            }
        }

        if (tickCount == 100 || !level().getBlockState(newPos).isAir()) {
            remove(RemovalReason.DISCARDED);
        }
    }
}
