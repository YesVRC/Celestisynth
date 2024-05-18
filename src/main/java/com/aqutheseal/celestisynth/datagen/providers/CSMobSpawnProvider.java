package com.aqutheseal.celestisynth.datagen.providers;

import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSMobSpawns;
import com.aqutheseal.celestisynth.common.registry.CSStructureModifiers;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.common.world.StructureModifier;

import java.util.List;

public class CSMobSpawnProvider {
    public static class StructureModifiers {
        public static void bootstrap(BootstapContext<StructureModifier> ctx) {
            final HolderGetter<Structure> structureRegistry = ctx.lookup(Registries.STRUCTURE);

            ctx.register(CSMobSpawns.NETHER_FORTRESS_SPAWNS, new CSStructureModifiers.AddSpawnsStructureModifier(
                    HolderSet.direct(structureRegistry.getOrThrow(BuiltinStructures.FORTRESS)), List.of(
                            new MobSpawnSettings.SpawnerData(CSEntityTypes.STAR_MONOLITH.get(), 2, 1, 1)
                    ))
            );
        }
    }

    public static class BiomeModifiers {
        public static void bootstrap(BootstapContext<BiomeModifier> ctx) {
            final HolderGetter<Biome> biomeRegistry = ctx.lookup(Registries.BIOME);

            ctx.register(CSMobSpawns.UNDERWATER_CAVE_SPAWNS, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                    HolderSet.direct(List.of(
                            biomeRegistry.getOrThrow(Biomes.OCEAN), biomeRegistry.getOrThrow(Biomes.COLD_OCEAN), biomeRegistry.getOrThrow(Biomes.DEEP_COLD_OCEAN), biomeRegistry.getOrThrow(Biomes.DEEP_OCEAN),
                            biomeRegistry.getOrThrow(Biomes.DEEP_LUKEWARM_OCEAN), biomeRegistry.getOrThrow(Biomes.DEEP_FROZEN_OCEAN), biomeRegistry.getOrThrow(Biomes.FROZEN_OCEAN),
                            biomeRegistry.getOrThrow(Biomes.WARM_OCEAN), biomeRegistry.getOrThrow(Biomes.LUKEWARM_OCEAN)
                    )), List.of(
                            new MobSpawnSettings.SpawnerData(CSEntityTypes.STAR_MONOLITH.get(), 10, 1, 1)
                    ))
            );
        }
    }
}
