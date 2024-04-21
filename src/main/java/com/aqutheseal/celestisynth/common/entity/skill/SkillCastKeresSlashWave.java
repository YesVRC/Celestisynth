package com.aqutheseal.celestisynth.common.entity.skill;

import com.aqutheseal.celestisynth.common.entity.base.EffectControllerEntity;
import com.aqutheseal.celestisynth.common.entity.projectile.KeresSlash;
import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSItems;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SkillCastKeresSlashWave extends EffectControllerEntity {
    public int lifespan = 15;

    public SkillCastKeresSlashWave(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public Item getCorrespondingItem() {
        return CSItems.KERES.get();
    }

    @Override
    public void tick() {
        super.tick();
        Player player = getOwnerUuid() == null ? null : level().getPlayerByUUID(getOwnerUuid());

        this.moveTo(player.position());

        if (lifespan > 0) {
            player.playSound(CSSoundEvents.SLASH_WATER.get(), 0.1F, (float) (1.5F + (player.getRandom().nextDouble() * 0.5)));
        }

        if (!level().isClientSide) {
            KeresSlash slash = new KeresSlash(CSEntityTypes.KERES_SLASH.get(), player, level());
            Vec3 vec31 = player.getUpVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0, vec31.x, vec31.y, vec31.z);
            Vec3 vec3 = player.getViewVector(1.0F);
            Vector3f shootAngle = vec3.toVector3f().rotate(quaternionf);
            slash.setRoll((float) (random.nextGaussian() * 360));
            slash.shoot(shootAngle.x, shootAngle.y, shootAngle.z, 4F, 0);
            level().addFreshEntity(slash);
        }

        if (tickCount > lifespan) {
            this.remove(RemovalReason.DISCARDED);
        }
    }
}
