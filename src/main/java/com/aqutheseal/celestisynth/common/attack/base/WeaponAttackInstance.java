package com.aqutheseal.celestisynth.common.attack.base;

import com.aqutheseal.celestisynth.api.animation.player.AnimationManager;
import com.aqutheseal.celestisynth.api.animation.player.LayerManager;
import com.aqutheseal.celestisynth.api.animation.player.PlayerAnimationContainer;
import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.registry.CSPlayerAnimations;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class WeaponAttackInstance implements CSWeaponUtil {
    public final Player player;
    public final Level level;
    public final ItemStack stack;
    public final int heldDuration;
    public static final SoundEvent[] BASE_WEAPON_EFFECTS = {
            CSSoundEvents.SWORD_SWING.get(),
            CSSoundEvents.SWORD_SWING_FIRE.get(),
            CSSoundEvents.AIR_SWING.get(),
            CSSoundEvents.SWORD_CLASH.get(),
            CSSoundEvents.FIRE_SHOOT.get(),
            CSSoundEvents.IMPACT_HIT.get()
    };

    public WeaponAttackInstance(Player player, ItemStack stack, int heldDuration) {
        this.player = player;
        this.level = player.level();
        this.stack = stack;
        this.heldDuration = heldDuration;
    }

    public WeaponAttackInstance(Player player, ItemStack stack) {
        this(player, stack, 0);
    }

    public abstract PlayerAnimationContainer getAnimation();

    public boolean sameAnimationForBothHands() {
        return false;
    }

    public abstract int getCooldown();

    public abstract int getAttackStopTime();

    public abstract boolean getCondition();

    public abstract void startUsing();

    public abstract void tickAttack();

    public abstract void stopUsing();

    public void baseStart() {
        startUsing();
    }

    public void baseStop() {
        stopUsing();
        AnimationManager.playAnimation(level, CSPlayerAnimations.CLEAR.get());
        AnimationManager.playAnimation(level, CSPlayerAnimations.CLEAR.get(), LayerManager.MIRRORED_LAYER);
        getTagController().putInt("cs.AttackIndex", -1);
        getTagController().putInt(ANIMATION_TIMER_KEY, 0);
        getTagController().putBoolean(ANIMATION_BEGUN_KEY, false);
        player.stopUsingItem();
    }

    public void baseTickSkill() {
        tickAttack();
        if (getTimerProgress() >= getAttackStopTime()) {
            baseStop();
        }
    }

    public Level getLevel() {
        return level;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getHeldDuration() {
        return heldDuration;
    }

    public int getTimerProgress() {
        return getTagController().getInt(ANIMATION_TIMER_KEY);
    }

    public CompoundTag getTagController() {
        return stack.getOrCreateTagElement(CS_CONTROLLER_TAG_ELEMENT);
    }

    public CompoundTag getTagExtras() {
        return stack.getOrCreateTagElement(CS_EXTRAS_ELEMENT);
    }

    public static void playRandomBladeSound(Entity entity, int length) {
        SoundEvent randomSound = BASE_WEAPON_EFFECTS[entity.level().getRandom().nextInt(length)];
        entity.playSound(randomSound, 0.15F, 0.5F + entity.level().getRandom().nextFloat());
    }
}
