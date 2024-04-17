package com.aqutheseal.celestisynth.common.attack.keres;

import com.aqutheseal.celestisynth.api.animation.player.PlayerAnimationContainer;
import com.aqutheseal.celestisynth.common.attack.base.WeaponAttackInstance;
import com.aqutheseal.celestisynth.common.entity.projectile.KeresRend;
import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSPlayerAnimations;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class KeresRendAttack extends WeaponAttackInstance {
    public KeresRendAttack(Player player, ItemStack stack, int heldDuration) {
        super(player, stack, heldDuration);
    }

    @Override
    public PlayerAnimationContainer getAnimation() {
        return CSPlayerAnimations.ANIM_AQUAFLORA_PIERCE_RIGHT.get();
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public int getAttackStopTime() {
        return 20;
    }

    @Override
    public boolean getCondition() {
        return heldDuration >= 200;
    }

    @Override
    public void startUsing() {
        if (!level.isClientSide) {
            KeresRend rend = new KeresRend(CSEntityTypes.KERES_REND.get(), player, level);
            rend.moveTo(player.position().add(calculateXLook(player) * 3, -3, calculateZLook(player) * 3));
            Vec3 vec31 = player.getUpVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0, vec31.x, vec31.y, vec31.z);
            Vec3 vec3 = player.getViewVector(1.0F);
            Vector3f shootAngle = vec3.toVector3f().rotate(quaternionf);
            rend.shoot(shootAngle.x, 0, shootAngle.z, 0.5F, 0);
            level.addFreshEntity(rend);
        }
    }

    @Override
    public void tickAttack() {
    }

    @Override
    public void stopUsing() {
    }
}
