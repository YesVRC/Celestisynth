package com.aqutheseal.celestisynth.datagen.providers;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.common.registry.CSBlocks;
import com.aqutheseal.celestisynth.common.registry.CSDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CSTagsProvider {
    public static class BlockHandler extends BlockTagsProvider {

        public BlockHandler(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, Celestisynth.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(CSBlocks.SOLAR_CRYSTAL.get())
                    .add(CSBlocks.LUNAR_STONE.get())
                    .add(CSBlocks.ZEPHYR_DEPOSIT.get())
                    .add(CSBlocks.CELESTIAL_CRAFTING_TABLE.get())
            ;
            tag(BlockTags.NEEDS_DIAMOND_TOOL)
                    .add(CSBlocks.SOLAR_CRYSTAL.get())
                    .add(CSBlocks.LUNAR_STONE.get())
                    .add(CSBlocks.CELESTIAL_CRAFTING_TABLE.get())
            ;
            tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                    .add(CSBlocks.ZEPHYR_DEPOSIT.get())
            ;
        }
    }

    public static class ItemHandler extends ItemTagsProvider {

        public ItemHandler(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
            super(pOutput, pLookupProvider, pBlockTags, Celestisynth.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
        }
    }

    public static class DamageTypeHandler extends DamageTypeTagsProvider {

        public DamageTypeHandler(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pOutput, pLookupProvider, Celestisynth.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tag(DamageTypeTags.BYPASSES_INVULNERABILITY).add(CSDamageTypes.RAPID_PLAYER_ATTACK);
        }
    }
}
