package com.aqutheseal.celestisynth.common.registry;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.common.mobeffect.CSMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CSMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Celestisynth.MODID);

    public static final RegistryObject<MobEffect> HELLBANE = MOB_EFFECTS.register("hellbane", () -> (new CSMobEffect(MobEffectCategory.BENEFICIAL, 14270531))
            .addAttributeModifier(Attributes.ATTACK_SPEED, "fe8ea578-5547-437b-8471-e08d8b34f5c8", 0.5F, AttributeModifier.Operation.MULTIPLY_TOTAL)
    );
}
