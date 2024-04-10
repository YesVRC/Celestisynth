package com.aqutheseal.celestisynth.mixin;

import com.aqutheseal.celestisynth.api.item.CSGeoItem;
import com.aqutheseal.celestisynth.common.capabilities.CSItemStackCapabilityProvider;
import com.aqutheseal.celestisynth.util.SkinUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int itemSlot, boolean isSelected, CallbackInfo ci) {
        itemStack.getCapability(CSItemStackCapabilityProvider.CAPABILITY).ifPresent(data -> {
            if (SkinUtil.getAquaSkinWhitelist().contains(entity.getUUID())) {
                data.setSkinIndex(1);
            } else {
                data.setSkinIndex(0);
            }
        });
    }

    @Inject(method = "initializeClient", at = @At("TAIL"), remap = false)
    public void initializeClient(Consumer<IClientItemExtensions> consumer, CallbackInfo ci) {
        if (this instanceof CSGeoItem geoItem) {
            geoItem.initGeo(consumer);
        }
    }

    //    @Shadow public abstract int getUseDuration(ItemStack pStack);
//    @Unique private static final String ATTACK_INDEX_KEY = "cs.AttackIndex";
//
//    @Inject(method = "<init>", at = @At("TAIL"))
//    private void init(Item.Properties pProperties, CallbackInfo ci) {
//        if (this instanceof CSWeapon weapon) {
//            if ((IForgeItem) this instanceof SwordItem sword) {
//                ImmutableMultimap.Builder<Attribute, AttributeModifier> additional = ImmutableMultimap.builder();
//                additional.putAll(sword.defaultModifiers);
//                weapon.addExtraAttributes(additional);
//                sword.defaultModifiers = additional.build();
//            } else if ((IForgeItem) this instanceof DiggerItem digger) {
//                ImmutableMultimap.Builder<Attribute, AttributeModifier> additional = ImmutableMultimap.builder();
//                additional.putAll(digger.defaultModifiers);
//                weapon.addExtraAttributes(additional);
//                digger.defaultModifiers = additional.build();
//            }
//        }
//    }
//
//    @Inject(method = "use", at = @At("HEAD"))
//    public void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
//        if (this instanceof CSWeapon weapon) {
//            ItemStack heldStack = player.getItemInHand(interactionHand);
//            CompoundTag data = heldStack.getOrCreateTagElement(CSWeaponUtil.CS_CONTROLLER_TAG_ELEMENT);
//
//            if (!player.getCooldowns().isOnCooldown(heldStack.getItem()) && !data.getBoolean(CSWeaponUtil.ANIMATION_BEGUN_KEY)) {
//                if (getUseDuration(heldStack) <= 0) {
//                    int index = 0;
//                    for (WeaponAttackInstance attack : weapon.getPossibleAttacks(player, heldStack, 0)) {
//                        if (attack.getCondition()) {
//                            data.putBoolean(CSWeaponUtil.ANIMATION_BEGUN_KEY, true);
//                            AnimationManager.playAnimation(level, attack.getAnimation());
//                            setAttackIndex(heldStack, index);
//                            attack.baseStart();
//                            player.getCooldowns().addCooldown(heldStack.getItem(), attack.getCooldown());
//                            break;
//                        }
//                        index++;
//                    }
//                } else {
//                    if (player.getCooldowns().isOnCooldown(heldStack.getItem()) || data.getBoolean(CSWeaponUtil.ANIMATION_BEGUN_KEY)) {
//                        cir.setReturnValue(InteractionResultHolder.fail(heldStack));
//                    } else {
//                        player.startUsingItem(interactionHand);
//                        cir.setReturnValue(InteractionResultHolder.consume(heldStack));
//                    }
//                }
//            }
//            cir.setReturnValue(InteractionResultHolder.success(heldStack));
//        }
//    }
//
//    @Inject(method = "releaseUsing", at = @At("HEAD"))
//    public void releaseUsing(ItemStack itemstack, @NotNull Level level, @NotNull LivingEntity entity, int i, CallbackInfo ci) {
//        if (this instanceof CSWeapon weapon) {
//            CompoundTag data = itemstack.getOrCreateTagElement(CSWeaponUtil.CS_CONTROLLER_TAG_ELEMENT);
//            int dur = this.getUseDuration(itemstack) - i;
//
//            if (entity instanceof Player player) {
//                int index = 0;
//                for (WeaponAttackInstance attack : weapon.getPossibleAttacks(player, itemstack, dur)) {
//                    if (attack.getCondition()) {
//                        data.putBoolean(CSWeaponUtil.ANIMATION_BEGUN_KEY, true);
//                        AnimationManager.playAnimation(level, attack.getAnimation());
//                        setAttackIndex(itemstack, index);
//                        attack.startUsing();
//                        player.getCooldowns().addCooldown(itemstack.getItem(), attack.getCooldown());
//                        break;
//                    }
//                    index++;
//                }
//            }
//        }
//    }
//
//    @Inject(method = "inventoryTick", at = @At("HEAD"))
//    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int itemSlot, boolean isSelected, CallbackInfo ci) {
//        itemStack.getCapability(CSItemStackCapabilityProvider.CAPABILITY).ifPresent(data -> {
//            if (SkinUtil.getAquaSkinWhitelist().contains(entity.getUUID())) {
//                data.setSkinIndex(1);
//            } else {
//                data.setSkinIndex(0);
//            }
//        });
//
//        if (this instanceof CSWeapon weapon) {
//            CompoundTag data = itemStack.getOrCreateTagElement(CSWeaponUtil.CS_CONTROLLER_TAG_ELEMENT);
//            if (entity instanceof Player player && data.getBoolean(CSWeaponUtil.ANIMATION_BEGUN_KEY)) {
//                int animationTimer = data.getInt(CSWeaponUtil.ANIMATION_TIMER_KEY);
//                data.putInt(CSWeaponUtil.ANIMATION_TIMER_KEY, animationTimer + 1);
//                int index = 0;
//                for (WeaponAttackInstance attack : weapon.getPossibleAttacks(player, itemStack, 0)) {
//                    if (getAttackIndex(itemStack) == index) {
//                        attack.baseTickSkill();
//                    }
//                    index++;
//                }
//            }
//        }
//    }
//
//    private int getAttackIndex(ItemStack stack) {
//        CompoundTag data = stack.getOrCreateTagElement(CSWeaponUtil.CS_CONTROLLER_TAG_ELEMENT);
//        return data.getInt(ATTACK_INDEX_KEY);
//    }
//
//    private void setAttackIndex(ItemStack stack, int value) {
//        CompoundTag data = stack.getOrCreateTagElement(CSWeaponUtil.CS_CONTROLLER_TAG_ELEMENT);
//        data.putInt(ATTACK_INDEX_KEY, value);
//    }
}
