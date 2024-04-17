package com.aqutheseal.celestisynth.common.attack.keres;

import com.aqutheseal.celestisynth.api.animation.player.PlayerAnimationContainer;
import com.aqutheseal.celestisynth.common.attack.base.WeaponAttackInstance;
import com.aqutheseal.celestisynth.common.entity.projectile.KeresSlash;
import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSPlayerAnimations;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class KeresSlashAttack extends WeaponAttackInstance {
    public KeresSlashAttack(Player player, ItemStack stack, int heldDuration) {
        super(player, stack, heldDuration);
    }

    @Override
    public PlayerAnimationContainer getAnimation() {
        return CSPlayerAnimations.ANIM_KERES_SLASH.get();
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
        player.playSound(CSSoundEvents.SLASH_WATER.get(), 0.2F, (float) (1.5F + (player.getRandom().nextDouble() * 0.5)));
        if (!level.isClientSide) {
            KeresSlash slash = new KeresSlash(CSEntityTypes.KERES_SLASH.get(), player, level);
            Vec3 vec31 = player.getUpVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0, vec31.x, vec31.y, vec31.z);
            Vec3 vec3 = player.getViewVector(1.0F);
            Vector3f shootAngle = vec3.toVector3f().rotate(quaternionf);
            slash.setRoll((float) (level.random.nextGaussian() * 360));
            slash.shoot(shootAngle.x, shootAngle.y, shootAngle.z, 1.5F, 0);
            level.addFreshEntity(slash);
        }
    }

    @Override
    public void tickAttack() {
    }

    @Override
    public void stopUsing() {
    }
}
