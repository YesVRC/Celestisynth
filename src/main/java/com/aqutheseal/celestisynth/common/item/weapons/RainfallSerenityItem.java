package com.aqutheseal.celestisynth.common.item.weapons;

import com.aqutheseal.celestisynth.api.animation.player.AnimationManager;
import com.aqutheseal.celestisynth.api.item.CSGeoItem;
import com.aqutheseal.celestisynth.api.item.CSWeapon;
import com.aqutheseal.celestisynth.api.item.CSWeaponUtil;
import com.aqutheseal.celestisynth.common.attack.base.WeaponAttackInstance;
import com.aqutheseal.celestisynth.common.capabilities.CSEntityCapabilityProvider;
import com.aqutheseal.celestisynth.common.compat.apotheosis.CSCompatAP;
import com.aqutheseal.celestisynth.common.entity.base.CSEffectEntity;
import com.aqutheseal.celestisynth.common.entity.helper.CSVisualAnimation;
import com.aqutheseal.celestisynth.common.entity.mob.misc.RainfallTurret;
import com.aqutheseal.celestisynth.common.entity.projectile.RainfallArrow;
import com.aqutheseal.celestisynth.common.registry.*;
import com.aqutheseal.celestisynth.manager.CSIntegrationManager;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class RainfallSerenityItem extends BowItem implements CSWeapon, CSGeoItem {
    public static CSVisualAnimation SPECIAL_RAINFALL = new CSVisualAnimation("animation.cs_effect.special_rainfall", 50);
    public static final String PULL = "cs.pull";
    public static final String PULLING = "cs.pulling";

    public RainfallSerenityItem(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public String geoIdentifier() {
        return "rainfall_serenity";
    }

    @Override
    public String texture(ItemStack stack) {
        float pull = attackExtras(stack).getFloat(PULL);
        float pulling = attackExtras(stack).getFloat(PULLING);
        if (pulling == 1 && pull >= 1) {
            return "rainfall_serenity_pulling_2";
        } else if (pulling == 1 && pull >= 0.5) {
            return "rainfall_serenity_pulling_1";
        } else if (pulling >= 1) {
            return "rainfall_serenity_pulling_0";
        } else  {
            return "rainfall_serenity";
        }
    }

    @Override
    public GeoAnimatable cacheItem() {
        return this;
    }

    @Override
    public int getPassiveAmount() {
        return 0;
    }

    @Override
    public int getSkillsAmount() {
        return 4;
    }

    public void addExtraAttributes(ImmutableMultimap.Builder<Attribute, AttributeModifier> map) {
        if (CSIntegrationManager.checkIronsSpellbooks()) {
            map.put(AttributeRegistry.SPELL_POWER.get(), new AttributeModifier(UUID.randomUUID(), "Item spell power", 0.075, AttributeModifier.Operation.MULTIPLY_BASE));
            map.put(AttributeRegistry.MANA_REGEN.get(), new AttributeModifier(UUID.randomUUID(), "Item mana regen", 0.1, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    @Override
    public ImmutableList<WeaponAttackInstance> getPossibleAttacks(Player player, ItemStack stack, int useDuration) {
        return null;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack heldStack = pPlayer.getItemInHand(pHand);
        CompoundTag elementData = heldStack.getOrCreateTagElement(CS_CONTROLLER_TAG_ELEMENT);
        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(heldStack, pLevel, pPlayer, pHand, true);

        if (ret != null) return ret;

        if (!pPlayer.isShiftKeyDown()) {
            pPlayer.startUsingItem(pHand);
            elementData.putBoolean(ANIMATION_BEGUN_KEY, true);
            if (pHand == InteractionHand.MAIN_HAND) {
                AnimationManager.playAnimation(pLevel, CSPlayerAnimations.ANIM_RAINFALL_AIM_RIGHT.get());
            } else {
                AnimationManager.playAnimation(pLevel, CSPlayerAnimations.ANIM_RAINFALL_AIM_LEFT.get());
            }

            return InteractionResultHolder.consume(heldStack);

        } else {
            if (!pPlayer.getCooldowns().isOnCooldown(this)) {
                shiftSkill(pLevel, pPlayer);
                pPlayer.getCooldowns().addCooldown(this, 200);
                return InteractionResultHolder.success(heldStack);
            }

            return InteractionResultHolder.fail(heldStack);
        }
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        if (pEntity instanceof LivingEntity living) {
            if (living.getUseItem() != pStack) {
                attackExtras(pStack).putFloat(PULL, 0);
                attackExtras(pStack).putFloat(PULLING, 0);
            } else {
                attackExtras(pStack).putFloat(PULL, (pStack.getUseDuration() - living.getUseItemRemainingTicks()) / ((RainfallSerenityItem) pStack.getItem()).getDrawSpeed(living, pStack));
                attackExtras(pStack).putFloat(PULLING, 1);
            }
        }
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        pLivingEntity.setYBodyRot(pLivingEntity.getYRot());
    }

    public void shiftSkill(Level level, Player player) {
        CSEffectEntity.createInstance(player, null, CSVisualTypes.RAINFALL_VANISH.get(), calculateXLook(player) * 3, 1, calculateZLook(player) * 3);
        CSEffectEntity.createInstance(player, null, CSVisualTypes.RAINFALL_VANISH_CIRCLE.get(), 0, -1.5, 0);
        player.playSound(CSSoundEvents.VANISH.get());
        player.addEffect(CSWeaponUtil.nonVisiblePotionEffect(MobEffects.MOVEMENT_SPEED, 100, 3));
        player.addEffect(CSWeaponUtil.nonVisiblePotionEffect(MobEffects.INVISIBILITY, 100, 0));
        CSEntityCapabilityProvider.get(player).ifPresent(data -> data.setTrueInvisibility(100));
        if (level instanceof ServerLevel server) {
            for (Mob mob : StreamSupport.stream(server.getAllEntities().spliterator(), false).filter(e -> e instanceof Mob).map(Mob.class::cast).filter(m -> m.getTarget() == player).toList()) {
                mob.setTarget(null);
            }
        }
    }

    public void release(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft, Player player, float charge, ArrowItem arrowitem, ItemStack projectileStack, AbstractArrow existingArrow) {
        int mult = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, pStack);
        if (mult > 0) {
            for (int i = 1; i < mult + 1; i++) {
                createAndShootArrow(pLevel, player, charge, existingArrow, arrowitem, projectileStack, 15F * i);
                createAndShootArrow(pLevel, player, charge, existingArrow, arrowitem, projectileStack, -15F * i);
            }
        }
    }

    private void createAndShootArrow(Level pLevel, Player player, float charge, AbstractArrow existingArrow, ArrowItem arrowitem, ItemStack projectileStack, float yOffset) {
        AbstractArrow clone = arrowitem.createArrow(pLevel, projectileStack, player);
        clone = this.customArrow(clone);
        this.transferPropertiesToClone(existingArrow, clone);

        Vec3 vec31 = player.getUpVector(1.0F);
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(yOffset * Mth.DEG_TO_RAD, vec31.x, vec31.y, vec31.z);
        Vec3 vec3 = player.getViewVector(1.0F);
        Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
        clone.shoot(vector3f.x(), vector3f.y(), vector3f.z(), charge * 3.0F, 1.0F);

        clone.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        pLevel.addFreshEntity(clone);
    }

    public void transferPropertiesToClone(AbstractArrow existingArrow, AbstractArrow clone) {
        Class<?> existingArrowClass = existingArrow.getClass();
        while (existingArrowClass != null) {
            for (Field field : existingArrowClass.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    if (Modifier.isPublic(field.getModifiers()) || field.getName().equals("remainingFireTicks")) {
                        field.set(clone, field.get(existingArrow));
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
            existingArrowClass = existingArrowClass.getSuperclass();
        }
        CompoundTag compoundTag = new CompoundTag();
        existingArrow.addAdditionalSaveData(compoundTag);
        clone.readAdditionalSaveData(compoundTag);
    }

    public void releaseUsingEffect(ItemStack pStack, Level pLevel, LivingEntity player, int pTimeLeft) {
        CompoundTag elementData = pStack.getOrCreateTagElement(CS_CONTROLLER_TAG_ELEMENT);
        int useDuration = getUseDuration(pStack) - pTimeLeft;
        double curPowerFromUse = getPowerForTime(player, pStack, useDuration);
        AnimationManager.playAnimation(pLevel, CSPlayerAnimations.CLEAR.get());
        elementData.putBoolean(ANIMATION_BEGUN_KEY, false);
        if (curPowerFromUse >= 1.0D) {
            if (curPowerFromUse == 1.0F) {
                CSEffectEntity.createInstance(player, null, CSVisualTypes.RAINFALL_SHOOT.get(), calculateXLook(player) * 2, 0.5 + (calculateYLook(player, 5) * 1), calculateZLook(player) * 2);
                player.setDeltaMovement(player.getDeltaMovement().subtract(calculateXLook(player) * 0.5, 0, calculateZLook(player) * 0.5));
            }
            int amount = 50;
            float expansionMultiplier = 0.5F;

            for (int e = 0; e < amount; e++) {
                RandomSource random = player.getRandom();
                double angle = random.nextDouble() * 2 * Math.PI;
                float offX = (float) Math.cos(angle) * expansionMultiplier;
                float offY = (-0.5f + random.nextFloat()) * expansionMultiplier;
                float offZ = (float) Math.sin(angle) * expansionMultiplier;

                ParticleUtil.sendParticles(pLevel, CSParticleTypes.RAINFALL_ENERGY_SMALL.get(), player.getX(), player.getY(), player.getZ(), 0, offX, offY, offZ);
            }
        }
        pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), CSSoundEvents.LASER_SHOOT.get(), SoundSource.PLAYERS, (float) (0.7F * curPowerFromUse), (float) (1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + curPowerFromUse * 0.5F));

    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        this.releaseUsingEffect(pStack, pLevel, pEntityLiving, pTimeLeft);
        super.releaseUsing(pStack, pLevel, pEntityLiving, pTimeLeft);
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        RainfallArrow rainfallArrow = new RainfallArrow(arrow);

        rainfallArrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        rainfallArrow.setOrigin(arrow.getOwner().position());
        rainfallArrow.setPierceLevel((byte) 3);
        rainfallArrow.setImbueQuasar(true);
        rainfallArrow.setStrong(true);

        if (CSIntegrationManager.checkApothicAttributes() && arrow.getOwner() instanceof LivingEntity owner) {
            rainfallArrow.setBaseDamage(rainfallArrow.getBaseDamage() + CSCompatAP.apothValue(ALObjects.Attributes.ARROW_VELOCITY, owner));
        }

        return rainfallArrow;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Entity owner = entity.level().getEntity(attackExtras(stack).getInt("ownerRF"));
        if (entity.onGround() && owner != null && stack.getDamageValue() < stack.getMaxDamage() - 1) {
            entity.playSound(SoundEvents.ENDER_EYE_DEATH);
            if (!entity.level().isClientSide) {
                if (owner instanceof Player player) {
                    RainfallTurret turret = CSEntityTypes.RAINFALL_TURRET.get().create(entity.level());
                    turret.moveTo(entity.position());
                    turret.setOwner(player);
                    double healthAdd = turret.getMaxHealth() + ((EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack)) * 50);
                    turret.getAttribute(Attributes.MAX_HEALTH).setBaseValue(healthAdd);
                    turret.setHealth(turret.getMaxHealth());
                    entity.level().addFreshEntity(turret);
                    turret.setItemData(stack.serializeNBT());
                    for (int i = 0; i < 16; i++) {
                        ParticleUtil.sendParticle(entity.level(), CSParticleTypes.RAINFALL_ENERGY_SMALL.get(),
                                entity.position().add(entity.level().random.nextGaussian() * 0.4, 0, entity.level().random.nextGaussian() * 0.4),
                                Vec3.ZERO.add(0, entity.level().random.nextDouble() * 0.65, 0));
                    }
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, Player player) {
        attackExtras(stack).putInt("ownerRF", player.getId());
        return super.onDroppedByPlayer(stack, player);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantments.POWER_ARROWS);
        enchantments.add(Enchantments.PUNCH_ARROWS);
        enchantments.add(Enchantments.FLAMING_ARROWS);
        enchantments.add(Enchantments.MULTISHOT);
        enchantments.add(Enchantments.PIERCING);
        enchantments.add(Enchantments.QUICK_CHARGE);

        if (enchantment == Enchantments.MULTISHOT || enchantment == Enchantments.QUICK_CHARGE) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, stack) > 0) {
                return false;
            }
        }

        if (enchantment == Enchantments.PIERCING) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, stack) > 0 || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack) > 0) {
                return false;
            }
        }

        if (enchantments.contains(enchantment)) return true;

        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    public float getDrawSpeed(LivingEntity entity, ItemStack stack) {
        float piercingEnchLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, stack);
        float quickEnchLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);

        float apoth_drawSpeed = CSCompatAP.apothRainfallSerenityDrawSpeed(entity);

        return (7.5F + (piercingEnchLvl * 10)) / ((quickEnchLvl + 1) * 0.6f) + apoth_drawSpeed;
    }

    public static float getPowerForTime(LivingEntity pEntityLiving, ItemStack stack, int pCharge) {
        float totalCharge = (float) pCharge / ((RainfallSerenityItem) stack.getItem()).getDrawSpeed(pEntityLiving, stack);
        totalCharge = (totalCharge * totalCharge + totalCharge * 2.0F) / 3.0F;

        if (totalCharge > 1.0F) totalCharge = 1.0F;

        return totalCharge;
    }

    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }
}
