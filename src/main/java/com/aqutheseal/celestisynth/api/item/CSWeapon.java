package com.aqutheseal.celestisynth.api.item;

import com.aqutheseal.celestisynth.common.attack.base.WeaponAttackInstance;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public interface CSWeapon extends CSWeaponUtil {

    int getSkillsAmount();

    default int getPassiveAmount() {
        return 0;
    }

    default boolean hasPassive() {
        return false;
    }

    default void onPlayerHurt(LivingHurtEvent event, ItemStack stack) {
    }

    default void resetExtraValues(ItemStack stack, Player player) {
    }

    default void addExtraAttributes(ImmutableMultimap.Builder<Attribute, AttributeModifier> map) {
    }

    ImmutableList<WeaponAttackInstance> getPossibleAttacks(Player player, ItemStack stack, int useDuration);
}
