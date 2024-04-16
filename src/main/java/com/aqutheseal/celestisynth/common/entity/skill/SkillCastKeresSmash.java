package com.aqutheseal.celestisynth.common.entity.skill;

import com.aqutheseal.celestisynth.api.item.AttackHurtTypes;
import com.aqutheseal.celestisynth.common.entity.base.CSEffectEntity;
import com.aqutheseal.celestisynth.common.entity.base.EffectControllerEntity;
import com.aqutheseal.celestisynth.common.registry.CSItems;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import com.aqutheseal.celestisynth.common.registry.CSVisualTypes;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class SkillCastKeresSmash extends EffectControllerEntity {
    public SkillCastKeresSmash(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        UUID ownerUuid = getOwnerUuid();
        Player player = ownerUuid == null ? null : level().getPlayerByUUID(ownerUuid);

        if (player == null) return;

        if (tickCount == 1) {
            CSEffectEntity.createInstance(player, null, CSVisualTypes.KERES_PULSE.get(), 0, 0.35, 0);
            this.doSmashAttack(player, 3, 0);
        }
        if (tickCount == 6) {
            CSEffectEntity.createInstance(player, null, CSVisualTypes.KERES_PULSE_1.get(), 0, -0.35, 0);
            this.doSmashAttack(player, 5, 1.5);
        }
        if (tickCount == 11) {
            CSEffectEntity.createInstance(player, null, CSVisualTypes.KERES_PULSE_2.get(), 0, -1.45, 0);
            this.remove(RemovalReason.DISCARDED);
            this.doSmashAttack(player, 7, 3);
        }
    }

    public void doSmashAttack(Player owner, double radius, double out) {
        playSound(CSSoundEvents.STEP.get(), 0.4F, 0.5F);
        Predicate<LivingEntity> filter = target -> target != owner && this.distanceToSqr(target) >= out;
        this.shakeScreensForNearbyPlayers(owner, level(), radius, 5, 5,  0.015F);
        for (int i = 0; i < 360; i = i + 2) {
            ParticleUtil.sendParticle(level(), CSParticleTypes.KERES_OMEN.get(), getX() + (Mth.sin(i) * radius), getY(), getZ() + (Mth.cos(i) * radius), 0, 0.5, 0);
        }
        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, new AABB(-radius, 0, -radius, radius, 4, radius).move(position())).stream().filter(filter).toList();
        for (LivingEntity target : targets) {
            this.initiateAbilityAttack(owner, target, 5F, AttackHurtTypes.RAPID);
        }
    }

    @Override
    public Item getCorrespondingItem() {
        return CSItems.KERES.get();
    }
}
