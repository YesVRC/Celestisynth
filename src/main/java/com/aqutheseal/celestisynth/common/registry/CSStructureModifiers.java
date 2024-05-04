package com.aqutheseal.celestisynth.common.registry;

import com.aqutheseal.celestisynth.Celestisynth;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.common.world.StructureSettingsBuilder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Function;

public class CSStructureModifiers {
    public static final Codec<HolderSet<Structure>> LIST_CODEC = RegistryCodecs.homogeneousList(Registries.STRUCTURE, Structure.DIRECT_CODEC);

    public static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, Celestisynth.MODID);

    public static final ResourceKey<StructureModifier> NETHER_FORTRESS_SPAWNS = ResourceKey.create(ForgeRegistries.Keys.STRUCTURE_MODIFIERS, Celestisynth.prefix("nether_fortress_spawns"));

    public static final RegistryObject<Codec<AddSpawnsStructureModifier>> ADD_SPAWNS_STRUCTURE_CODEC = STRUCTURE_MODIFIER_SERIALIZERS.register("add_spawns", () ->
            RecordCodecBuilder.create(builder -> builder.group(
                    LIST_CODEC.fieldOf("structures").forGetter(AddSpawnsStructureModifier::structures),
                    new ExtraCodecs.EitherCodec<>(MobSpawnSettings.SpawnerData.CODEC.listOf(), MobSpawnSettings.SpawnerData.CODEC).xmap(
                            either -> either.map(Function.identity(), List::of),
                            list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list)
                            ).fieldOf("spawners").forGetter(AddSpawnsStructureModifier::spawners)
            ).apply(builder, AddSpawnsStructureModifier::new))
    );


    public record AddSpawnsStructureModifier(HolderSet<Structure> structures, List<MobSpawnSettings.SpawnerData> spawners) implements StructureModifier {
        @Override
        public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
            if(phase == Phase.ADD && this.structures.contains(structure)) {
                StructureSettingsBuilder structureSettings = builder.getStructureSettings();
                for (MobSpawnSettings.SpawnerData spawner : this.spawners) {
                    EntityType<?> type = spawner.type;
                    structureSettings.getOrAddSpawnOverrides(type.getCategory()).addSpawn(spawner);
                }
            }
        }

        @Override
        public Codec<? extends StructureModifier> codec() {
            return CSStructureModifiers.ADD_SPAWNS_STRUCTURE_CODEC.get();
        }
    }
}
