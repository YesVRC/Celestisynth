package com.aqutheseal.celestisynth.common.attack.keres;

import com.aqutheseal.celestisynth.api.animation.player.PlayerAnimationContainer;
import com.aqutheseal.celestisynth.common.attack.base.WeaponAttackInstance;
import com.aqutheseal.celestisynth.common.entity.skillcast.SkillCastKeresSlashWave;
import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSPlayerAnimations;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

public class KeresSlashAttack extends WeaponAttackInstance {
    public KeresSlashAttack(Player player, ItemStack stack, int heldDuration) {
        super(player, stack, heldDuration);
    }

    @Override
    public PlayerAnimationContainer getAnimation() {
        return switch (level.random.nextInt(3)) {
            default -> CSPlayerAnimations.ANIM_KERES_SLASH.get();
            case 1 -> CSPlayerAnimations.ANIM_KERES_SLASH_1.get();
            case 2 -> CSPlayerAnimations.ANIM_KERES_SLASH_2.get();
        };
    }

    @Override
    public boolean sameAnimationForBothHands() {
        return true;
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public int getAttackStopTime() {
        return 5;
    }

    @Override
    public boolean getCondition() {
        return !player.isShiftKeyDown() && heldDuration < 200;
    }

    @Override
    public void startUsing() {
        player.playSound(CSSoundEvents.SLASH_WATER.get(), 0.1F, (float) (1.5F + (player.getRandom().nextDouble() * 0.5)));
        if (!level.isClientSide) {
            SkillCastKeresSlashWave wave = CSEntityTypes.KERES_SLASH_WAVE.get().create(level);
            wave.setOwner(player);
            wave.moveTo(player.position());
            wave.damage = this.calculateAttributeDependentDamage(player, stack, 0.1F);
            wave.lifespan = (heldDuration / 6) + level.random.nextInt(3);
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack) > 0) {
                wave.hasMultishot = true;
            }
            level.addFreshEntity(wave);
        }
        Vec3 add = player.getDeltaMovement().normalize().scale(player.onGround() ? 2 : 1.25);
        player.setDeltaMovement(add.x(), 0, add.z());
    }

    @Override
    public void tickAttack() {
    }

    @Override
    public void stopUsing() {
    }
}
