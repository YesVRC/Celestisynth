package com.aqutheseal.celestisynth.common.item.weapons;

import com.aqutheseal.celestisynth.api.animation.player.AnimationManager;
import com.aqutheseal.celestisynth.api.animation.player.LayerManager;
import com.aqutheseal.celestisynth.api.item.CSGeoItem;
import com.aqutheseal.celestisynth.api.mixin.PlayerMixinSupport;
import com.aqutheseal.celestisynth.common.attack.base.WeaponAttackInstance;
import com.aqutheseal.celestisynth.common.attack.keres.KeresRendAttack;
import com.aqutheseal.celestisynth.common.attack.keres.KeresSlashAttack;
import com.aqutheseal.celestisynth.common.attack.keres.KeresSmashAttack;
import com.aqutheseal.celestisynth.common.compat.bettercombat.SwingParticleContainer;
import com.aqutheseal.celestisynth.common.entity.projectile.KeresShadow;
import com.aqutheseal.celestisynth.common.item.base.SkilledSwordItem;
import com.aqutheseal.celestisynth.common.registry.*;
import com.aqutheseal.celestisynth.manager.CSIntegrationManager;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

import java.util.UUID;

public class KeresItem extends SkilledSwordItem implements CSGeoItem {
    public static final String PASSIVE_STACK = "cs.keresStack";

    public KeresItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public String geoIdentifier() {
        return "keres";
    }

    @Override
    public GeoAnimatable cacheItem() {
        return this;
    }

    @Override
    public void addExtraAttributes(ImmutableMultimap.Builder<Attribute, AttributeModifier> map) {
        if (CSIntegrationManager.checkIronsSpellbooks()) {
            map.put(AttributeRegistry.BLOOD_SPELL_POWER.get(), new AttributeModifier(UUID.randomUUID(), "Item blood spell power", 0.1, AttributeModifier.Operation.MULTIPLY_BASE));
            map.put(AttributeRegistry.BLOOD_MAGIC_RESIST.get(), new AttributeModifier(UUID.randomUUID(), "Item blood resist", 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    @Override
    public ImmutableList<WeaponAttackInstance> getPossibleAttacks(Player player, ItemStack stack, int dur) {
        return ImmutableList.of(
                new KeresSlashAttack(player, stack, dur),
                new KeresRendAttack(player, stack, dur),
                new KeresSmashAttack(player, stack, dur)
        );
    }

    @Override
    public @Nullable SwingParticleContainer getSwingContainer(LivingEntity holder, ItemStack stack) {
        if (holder.hasEffect(CSMobEffects.HELLBANE.get()) && holder.getRandom().nextInt(5) == 1) {
            return new SwingParticleContainer(CSParticleTypes.KERES_OMEN.get(), 2.8F);
        }
        return new SwingParticleContainer(CSParticleTypes.KERES_ASH.get(), 2.8F);
    }

    @Override
    public int getSkillsAmount() {
        return 2;
    }

    @Override
    public int getPassiveAmount() {
        return 1;
    }

    @Override
    public void startUsing(Level level, Player player, InteractionHand interactionHand) {
        super.startUsing(level, player, interactionHand);
        int layer = interactionHand == InteractionHand.OFF_HAND ? LayerManager.MAIN_LAYER : LayerManager.MIRRORED_LAYER;
        AnimationManager.playAnimation(level, CSPlayerAnimations.ANIM_KERES_CHARGE.get(), layer);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pEntity, pStack, pRemainingUseDuration);
        int dur = this.getUseDuration(pStack) - pRemainingUseDuration;

        if (dur % 20 == 0) {
            if (pEntity.getHealth() > 1 && !isCreativeOrSpectator(pEntity)) {
                pEntity.setHealth(pEntity.getHealth() - 1.5f);
                this.sacrificeEffect(pLevel, pEntity);
            } else {
                if (!isCreativeOrSpectator(pEntity)) {
                    if (pEntity instanceof Player player) {
                        player.getCooldowns().addCooldown(this, 20);
                        AnimationManager.playAnimation(pLevel, CSPlayerAnimations.CLEAR.get());
                        if (pLevel.isClientSide) {
                            player.displayClientMessage(Component.translatable("item.celestisynth.keres.notice").withStyle(ChatFormatting.RED), true);
                        }
                    }
                    pEntity.stopUsingItem();
                } else {
                    this.sacrificeEffect(pLevel, pEntity);
                }
            }
        }
//        if (pEntity instanceof Player player) {
//            if (player.isShiftKeyDown() && dur < 200) {
//                for (int i = 0; i < 360; i = i + 2) {
//                    ParticleUtil.sendParticle(pLevel, CSParticleTypes.KERES_OMEN.get(),
//                            player.getX() + (calculateXLook(player) * ((double) dur / 3)) + (Mth.sin(i) * 5),
//                            player.getY(),
//                            player.getZ() + (calculateZLook(player) * ((double) dur / 3)) + (Mth.cos(i) * 5),
//                            -Mth.sin(i) * 0.3 , 0.5, -Mth.cos(i) * 0.3
//                    );
//                }
//            }
//        }
        if (dur >= 200) {
            if (pEntity instanceof Player player) {
                shakeScreensForNearbyPlayers(player, pLevel, 12, 30, 15,  0.01F);
            }
            ParticleUtil.sendParticle(pLevel, ParticleTypes.FLASH, pEntity.getX(), pEntity.getY() + 4, pEntity.getZ());
        }
    }

    public void sacrificeEffect(Level pLevel, LivingEntity pEntity) {
        pEntity.playSound(CSSoundEvents.HEARTBEAT.get(), 0.5f, (float) (1.0 + (pLevel.random.nextGaussian() * 0.2)));
        if (pEntity instanceof PlayerMixinSupport mixinPlayer) {
            mixinPlayer.setKeresMark(-10);
            mixinPlayer.setKeresOrderX((int) pLevel.random.nextGaussian() * 10, (int) pLevel.random.nextGaussian() * 10, (int) pLevel.random.nextGaussian() * 10);
            mixinPlayer.setKeresOrderY((int) pLevel.random.nextGaussian() * 10, (int) pLevel.random.nextGaussian() * 10, (int) pLevel.random.nextGaussian() * 10);
        }
    }

    public boolean isCreativeOrSpectator(Entity target) {
        if (target instanceof Player player) {
            return player.isCreative() || player.isSpectator();
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(itemStack, level, entity, itemSlot, isSelected);

        if (isSelected) {
            if (entity instanceof LivingEntity source) {
                if (source.hasEffect(CSMobEffects.HELLBANE.get())) {
                    double xSin = Mth.sin((float) (0.2 * source.tickCount)) * 3;
                    double zCos = Mth.cos((float) (0.2 * source.tickCount)) * 3;
                    ParticleUtil.sendParticle(level, CSParticleTypes.KERES_ASH.get(), entity.getX() + xSin, entity.getY() + 1, entity.getZ() + zCos);
                    ParticleUtil.sendParticle(level, CSParticleTypes.KERES_ASH.get(), entity.getX() - xSin, entity.getY() + 1, entity.getZ() - zCos);

                    if (source.tickCount % 10 == 0) {
                        if (!level.isClientSide()) {
                            KeresShadow shadow = new KeresShadow(CSEntityTypes.KERES_SHADOW.get(), source, level);
                            shadow.moveTo(source.getX(), shadow.getY() - 1, source.getZ());

                            Entity lookAtTarget = getLookedAtEntity(source, 128);
                            LivingEntity observedLivingTarget = lookAtTarget instanceof LivingEntity observedLiving ? observedLiving : null;
                            if (observedLivingTarget != null) {
                                shadow.setHomingTarget(observedLivingTarget);
                            }
                            shadow.shootFromRotation(source, (float) (source.getRandom().nextGaussian() * 180), -15 - (float) (source.getRandom().nextDouble() * 75), 0, 1F, 0);
                            level.addFreshEntity(shadow);
                        }
                        source.playSound(SoundEvents.WITHER_SHOOT, 0.1F, 1.5F + (source.getRandom().nextFloat() * 0.5F));
                    }
                }
            }
        } else {
            this.attackController(itemStack).putInt(PASSIVE_STACK, 0);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity entity, LivingEntity source) {
        boolean flag = super.hurtEnemy(itemStack, entity, source);
        if (flag) {
            double lifesteal = 0.35;
            if (source.hasEffect(CSMobEffects.HELLBANE.get())) {
                lifesteal = 0.85;
            }
            source.heal((float) lifesteal);
            this.attackController(itemStack).putInt(PASSIVE_STACK, this.attackController(itemStack).getInt(PASSIVE_STACK) + 1);
            if (this.attackController(itemStack).getInt(PASSIVE_STACK) >= 5) {
                if (source instanceof Player player) {
                    player.getCooldowns().removeCooldown(this);
                }
                source.addEffect(new MobEffectInstance(CSMobEffects.HELLBANE.get(), 100, 0));
                this.attackController(itemStack).putInt(PASSIVE_STACK, 0);
            }
        }
        return flag;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }
}
