package com.aqutheseal.celestisynth.common.registry;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.common.entity.base.CSEffect;
import com.aqutheseal.celestisynth.common.entity.projectile.RainfallArrow;
import com.aqutheseal.celestisynth.common.entity.skill.SkillCastBreezebreakerTornado;
import com.aqutheseal.celestisynth.common.entity.skill.SkillCastCrescentiaRanged;
import com.aqutheseal.celestisynth.common.entity.skill.SkillCastPoltergeistWard;
import com.aqutheseal.celestisynth.common.entity.tempestboss.TempestBoss;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Celestisynth.MODID);

    public static final RegistryObject<EntityType<TempestBoss>> TEMPEST = ENTITY_TYPES.register("tempest", () -> EntityType.Builder.of(TempestBoss::new, MobCategory.MONSTER)
            .sized(0.7F, 1.95F)
            .clientTrackingRange(8)
            .build(Celestisynth.prefix("tempest").toString()));

    public static final RegistryObject<EntityType<CSEffect>> CS_EFFECT = ENTITY_TYPES.register("cs_effect", () -> EntityType.Builder.of(CSEffect::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(32)
            .build(Celestisynth.prefix("cs_effect").toString())
    );
    public static final RegistryObject<EntityType<SkillCastCrescentiaRanged>> CRESCENTIA_RANGED = ENTITY_TYPES.register("crescentia_ranged", () -> EntityType.Builder.of(SkillCastCrescentiaRanged::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(16)
            .build(Celestisynth.prefix("crescentia_ranged").toString())
    );
    public static final RegistryObject<EntityType<SkillCastBreezebreakerTornado>> BREEZEBREAKER_TORNADO = ENTITY_TYPES.register("breezebreaker_tornado", () -> EntityType.Builder.of(SkillCastBreezebreakerTornado::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(16)
            .build(Celestisynth.prefix("breezebreaker_tornado").toString())
    );
    public static final RegistryObject<EntityType<SkillCastPoltergeistWard>> POLTERGEIST_WARD = ENTITY_TYPES.register("poltergeist_ward", () -> EntityType.Builder.of(SkillCastPoltergeistWard::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(16)
            .build(Celestisynth.prefix("poltergeist_ward").toString())
    );

    public static final RegistryObject<EntityType<RainfallArrow>> RAINFALL_ARROW = ENTITY_TYPES.register("rainfall_arrow", () -> EntityType.Builder.<RainfallArrow>of(RainfallArrow::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(32)
            .updateInterval(20)
            .build(Celestisynth.prefix("rainfall_arrow").toString())
    );

}
