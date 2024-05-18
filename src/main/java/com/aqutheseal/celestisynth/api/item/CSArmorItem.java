package com.aqutheseal.celestisynth.api.item;

import com.aqutheseal.celestisynth.common.compat.spellbooks.ISSArmorUtil;
import com.aqutheseal.celestisynth.common.entity.base.CSEffectEntity;
import com.aqutheseal.celestisynth.common.registry.CSAttributes;
import com.aqutheseal.celestisynth.common.registry.CSParticleTypes;
import com.aqutheseal.celestisynth.common.registry.CSSoundEvents;
import com.aqutheseal.celestisynth.common.registry.CSVisualTypes;
import com.aqutheseal.celestisynth.manager.CSIntegrationManager;
import com.aqutheseal.celestisynth.util.ParticleUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CSArmorItem extends ArmorItem implements CSWeaponUtil {
    public CSArmorItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot pEquipmentSlot) {
        return pEquipmentSlot == this.type.getSlot() ? modifiedAttributes() : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    public void createExtraAttributes(ImmutableMultimap.Builder<Attribute, AttributeModifier> additional, UUID uuid) {
        if (material == CSArmorMaterials.SOLAR_CRYSTAL) {
            additional.put(CSAttributes.SOLAR_EXPLOSION_DAMAGE.get(), new AttributeModifier(uuid, "Solar explosion radius", 1, AttributeModifier.Operation.ADDITION));
            additional.put(CSAttributes.CELESTIAL_DAMAGE.get(), new AttributeModifier(uuid, "Armor celestial damage", 0.05, AttributeModifier.Operation.MULTIPLY_BASE));
            additional.put(CSAttributes.CELESTIAL_DAMAGE_REDUCTION.get(), new AttributeModifier(uuid, "Armor celestial damage reduction", 0.025, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (material == CSArmorMaterials.LUNAR_STONE) {
            additional.put(CSAttributes.LUNAR_BURST_REDUCTION.get(), new AttributeModifier(uuid, "Lunar burst reduction", 0.65, AttributeModifier.Operation.ADDITION));
            additional.put(CSAttributes.CELESTIAL_DAMAGE.get(), new AttributeModifier(uuid, "Armor celestial damage", 0.025, AttributeModifier.Operation.MULTIPLY_BASE));
            additional.put(CSAttributes.CELESTIAL_DAMAGE_REDUCTION.get(), new AttributeModifier(uuid, "Armor celestial damage reduction", 0.05, AttributeModifier.Operation.MULTIPLY_BASE));;
        }
    }

    public void hurtWearer(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        AttributeInstance solarAttribute = entity.getAttribute(CSAttributes.SOLAR_EXPLOSION_DAMAGE.get());
        assert solarAttribute != null;
        if (solarAttribute.getValue() > 0) {
            CSEffectEntity.createInstance(entity, entity, CSVisualTypes.SOLAR_EXPLOSION.get(), 0, 0.75, 0);
            entity.level().playSound(null, entity, CSSoundEvents.SWORD_SWING_FIRE.get(), SoundSource.PLAYERS, 0.2F, 1.0F);
            for (int i = 0; i < 22.5; i++) {
                Vec3 delta = new Vec3(0, 0, 0).add(Mth.sin(i), 0, Mth.cos(i)).scale(0.25);
                ParticleUtil.sendParticle(entity.level(), CSParticleTypes.SOLARIS_FLAME.get(), entity.position().add(0, 1.5, 0), delta);
            }
            for (LivingEntity targets : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(2, 0, 2)).stream().filter(en -> en != entity).toList()) {
                targets.hurt(entity.damageSources().onFire(), (float) solarAttribute.getValue() * 1.5F);
                targets.setSecondsOnFire((int) (1 + solarAttribute.getValue()));
            }
        }

        AttributeInstance lunarAttribute = entity.getAttribute(CSAttributes.LUNAR_BURST_REDUCTION.get());
        assert lunarAttribute != null;
        if (lunarAttribute.getValue() > 0) {
            if (event.getAmount() > 2) {
                event.setAmount((float) (event.getAmount() - lunarAttribute.getValue()));
            }
        }
    }

    public Multimap<Attribute, AttributeModifier> modifiedAttributes() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> additional = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(type);
        additional.putAll(this.defaultModifiers);
        this.createExtraAttributes(additional, uuid);
        if (CSIntegrationManager.checkIronsSpellbooks()) {
            ISSArmorUtil.addSpellbookAttributesOnArmor(additional, uuid, material);
        }
        return additional.build();
    }
}
