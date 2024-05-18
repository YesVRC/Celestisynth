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
import com.aqutheseal.celestisynth.manager.CSConfigManager;
import com.aqutheseal.celestisynth.manager.CSIntegrationManager;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class RainfallSerenityItem extends BowItem implements CSWeapon, CSGeoItem {
    public static CSVisualAnimation SPECIAL_RAINFALL = new CSVisualAnimation("animation.cs_effect.special_rainfall", 50);
    public Multimap<Attribute, AttributeModifier> defaultModifiers;
    public static final String PULL = "cs.pull";
    public static final String PULLING = "cs.pulling";

    public RainfallSerenityItem(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        this.addExtraAttributes(builder);
        this.defaultModifiers = builder.build();
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
        return 3;
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

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pSlot) {
        return pSlot == EquipmentSlot.MAINHAND ? defaultModifiers : super.getDefaultAttributeModifiers(pSlot);
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

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            CompoundTag elementData = pStack.getOrCreateTagElement(CS_CONTROLLER_TAG_ELEMENT);
            int useDuration = getUseDuration(pStack) - pTimeLeft;
            double curPowerFromUse = getPowerForTime(pEntityLiving, pStack, useDuration);

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

                FloatArrayList angles = new FloatArrayList();
                angles.add(0.0F);

                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, pStack) > 0) {
                    if (pLevel.random.nextBoolean()) {
                        angles.add(-15.0F);
                        angles.add(15.0F);
                    } else {
                        angles.add(-30.0F);
                        angles.add(30.0F);
                    }

                    if (pLevel.random.nextInt(3) == 0) angles.add(-30 + (pLevel.random.nextFloat() * 60.0F));
                }
                for (float angle : angles) {
                    if (!pLevel.isClientSide) {
                        RainfallArrow rainfallArrow = new RainfallArrow(pLevel, player);

                        rainfallArrow = (RainfallArrow) customArrow(rainfallArrow);
                        rainfallArrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        rainfallArrow.setOrigin(player.position());
                        rainfallArrow.setPierceLevel((byte) 3);
                        rainfallArrow.setBaseDamage(CSConfigManager.COMMON.rainfallSerenityArrowDmg.get() + (pLevel.random.nextDouble() * 3));
                        rainfallArrow.setImbueQuasar(true);

                        Vec3 vec31 = pEntityLiving.getUpVector(1.0F);
                        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(angle * ((float)Math.PI / 180F), vec31.x, vec31.y, vec31.z);
                        Vec3 vec3 =  pEntityLiving.getViewVector(1.0F);
                        Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);

                        rainfallArrow.shoot(vector3f.x(), vector3f.y(), vector3f.z(), 3.0F, 0);

                        int multishotEnchLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, pStack);

                        if (curPowerFromUse == 1.0F) rainfallArrow.setStrong(true);
                        installLaserProperties(rainfallArrow, pStack);

                        rainfallArrow.setBaseDamage(rainfallArrow.getBaseDamage() + CSCompatAP.apothRainfallSerenityDamage(pEntityLiving));
                        if (pLevel.random.nextFloat() < CSCompatAP.apothRainfallSerenityCritChance(pEntityLiving)) {
                            rainfallArrow.setBaseDamage(rainfallArrow.getBaseDamage() + CSCompatAP.apothRainfallSerenityCritDamage(pEntityLiving));
                        }

                        if (multishotEnchLvl > 0) rainfallArrow.setBaseDamage(rainfallArrow.getBaseDamage() * 0.75F);

                        pLevel.addFreshEntity(rainfallArrow);
                    }

                    pStack.hurtAndBreak(1, player, (livingOwner) -> livingOwner.broadcastBreakEvent(player.getUsedItemHand()));
                }

                pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), CSSoundEvents.LASER_SHOOT.get(), SoundSource.PLAYERS, (float) (0.7F * curPowerFromUse), (float) (1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + curPowerFromUse * 0.5F));
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public static void installLaserProperties(RainfallArrow rainfallArrow, ItemStack pStack) {
        int powerEnchLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, pStack);
        int piercingEnchLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, pStack);
        int punchEnchLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, pStack);
        int flameEnchLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, pStack);

        rainfallArrow.setBaseDamage(rainfallArrow.getBaseDamage() + powerEnchLvl);
        rainfallArrow.setBaseDamage(rainfallArrow.getBaseDamage() + (piercingEnchLvl * 4));
        if (punchEnchLvl > 0) rainfallArrow.setKnockback(punchEnchLvl);
        if (flameEnchLvl > 0) rainfallArrow.setFlaming(true);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.onGround() && stack.getDamageValue() < stack.getMaxDamage() - 1) {
            entity.playSound(SoundEvents.ENDER_EYE_DEATH);
            if (!entity.level().isClientSide) {
                Entity owner = entity.level().getEntity(attackExtras(stack).getInt("ownerRF"));
                if (owner instanceof Player player) {
                    RainfallTurret turret = CSEntityTypes.RAINFALL_TURRET.get().create(entity.level());
                    turret.moveTo(entity.position());
                    turret.setOwner(player);
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

        return (float) (CSConfigManager.COMMON.rainfallSerenityDrawSpeed.get() + (piercingEnchLvl * 10)) / ((quickEnchLvl + 1) * 0.6f) + apoth_drawSpeed;
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
