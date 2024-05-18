package com.aqutheseal.celestisynth.common.registry;

import com.aqutheseal.celestisynth.Celestisynth;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class CSMobSpawns {

    // Structure-Specific Spawn Modifiers
    public static final ResourceKey<StructureModifier> NETHER_FORTRESS_SPAWNS = ResourceKey.create(ForgeRegistries.Keys.STRUCTURE_MODIFIERS, Celestisynth.prefix("nether_fortress_spawns"));

    // Biome Spawn Modifiers
    public static final ResourceKey<BiomeModifier> UNDERWATER_CAVE_SPAWNS = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Celestisynth.prefix("underwater_cave_spawns"));
}
