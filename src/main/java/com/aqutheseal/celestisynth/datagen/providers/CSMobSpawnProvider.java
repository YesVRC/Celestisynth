package com.aqutheseal.celestisynth.datagen.providers;

import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSStructureModifiers;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.StructureModifier;

import java.util.List;

public class CSMobSpawnProvider {
    public static class StructureModifiers {
        public static void bootstrap(BootstapContext<StructureModifier> ctx) {
            final HolderGetter<Structure> structureRegistry = ctx.lookup(Registries.STRUCTURE);

            ctx.register(CSStructureModifiers.NETHER_FORTRESS_SPAWNS, new CSStructureModifiers.AddSpawnsStructureModifier(
                    HolderSet.direct(structureRegistry.getOrThrow(BuiltinStructures.FORTRESS)), List.of(
                            new MobSpawnSettings.SpawnerData(CSEntityTypes.STAR_MONOLITH.get(), 2, 1, 1)
                    ))
            );
        }
    }
}
