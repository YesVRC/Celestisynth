package com.aqutheseal.celestisynth.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class TargetUtil {
    private static <T extends LivingEntity & OwnableEntity> boolean isValidTargetForOwnableBase(T pet, LivingEntity potentialTarget) {
        if (potentialTarget == pet) {
            return false;
        }
        if (pet.getOwner() != null) {
            if (pet.getOwner() == potentialTarget) {
                return false;
            }
            if (potentialTarget instanceof OwnableEntity ownable) {
                if (pet.getOwner() == ownable.getOwner()) {
                    return false;
                }
            }
        }
        return !potentialTarget.isDeadOrDying() && !potentialTarget.isRemoved();
    }

    public static <T extends LivingEntity & OwnableEntity> boolean isValidTargetForOwnable(T pet, LivingEntity potentialTarget) {
        return isValidTargetForOwnableBase(pet, potentialTarget) && TargetingConditions.forCombat().test(pet, potentialTarget);
    }

    public static <T extends LivingEntity & OwnableEntity> TargetingConditions isValidTargetForOwnableCondition(T pet) {
        return TargetingConditions.forCombat().selector(target -> isValidTargetForOwnableBase(pet, target));
    }
}
