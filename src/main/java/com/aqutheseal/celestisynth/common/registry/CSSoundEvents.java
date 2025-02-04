package com.aqutheseal.celestisynth.common.registry;

import com.aqutheseal.celestisynth.Celestisynth;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Celestisynth.MODID);

    public static final RegistryObject<SoundEvent> STEP = createSound("step");
    public static final RegistryObject<SoundEvent> HOP = createSound("hop");
    public static final RegistryObject<SoundEvent> SWORD_SWING = createSound("sword_swing");
    public static final RegistryObject<SoundEvent> SWORD_SWING_FIRE = createSound("sword_swing_fire");
    public static final RegistryObject<SoundEvent> AIR_SWING = createSound("air_swing");
    public static final RegistryObject<SoundEvent> IMPACT_HIT = createSound("impact_hit");
    public static final RegistryObject<SoundEvent> SWORD_CLASH = createSound("sword_clash");
    public static final RegistryObject<SoundEvent> FIRE_SHOOT = createSound("fire_shoot");
    public static final RegistryObject<SoundEvent> WIND_STRIKE = createSound("wind_strike");
    public static final RegistryObject<SoundEvent> WHIRLWIND = createSound("whirlwind");
    public static final RegistryObject<SoundEvent> LOUD_IMPACT = createSound("loud_impact");
    public static final RegistryObject<SoundEvent> BLING = createSound("bling");
    public static final RegistryObject<SoundEvent> LASER_SHOOT = createSound("laser_shoot");
    public static final RegistryObject<SoundEvent> VANISH = createSound("vanish");
    public static final RegistryObject<SoundEvent> FROZEN_SLASH = createSound("frozen_slash");
    public static final RegistryObject<SoundEvent> ICE_CAST = createSound("ice_cast");
    public static final RegistryObject<SoundEvent> GROUND_IMPACT_WATER = createSound("ground_impact_water");
    public static final RegistryObject<SoundEvent> SLASH_WATER = createSound("slash_water");
    public static final RegistryObject<SoundEvent> WATER_CAST = createSound("water_cast");
    public static final RegistryObject<SoundEvent> BASS_DROP = createSound("bass_drop");
    public static final RegistryObject<SoundEvent> BASS_PULSE = createSound("bass_pulse");
    public static final RegistryObject<SoundEvent> HEARTBEAT = createSound("heartbeat");

    public static final RegistryObject<SoundEvent> TRAVERSER_DEATH = createSound("traverser_death");
    public static final RegistryObject<SoundEvent> TRAVERSER_HURT = createSound("traverser_hurt");
    public static final RegistryObject<SoundEvent> TRAVERSER_STEP = createSound("traverser_step");

    public static RegistryObject<SoundEvent> createSound(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Celestisynth.prefix(name)));
    }
}
