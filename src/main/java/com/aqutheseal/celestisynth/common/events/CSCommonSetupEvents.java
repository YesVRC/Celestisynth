package com.aqutheseal.celestisynth.common.events;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.common.entity.helper.CSVisualType;
import com.aqutheseal.celestisynth.common.entity.tempestboss.TempestBoss;
import com.aqutheseal.celestisynth.common.registry.CSCapabilities;
import com.aqutheseal.celestisynth.common.registry.CSEntityTypes;
import com.aqutheseal.celestisynth.common.registry.CSItems;
import com.aqutheseal.celestisynth.common.registry.CSVisualTypes;
import com.aqutheseal.celestisynth.datagen.providers.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CSCommonSetupEvents {
    
    public static class CSForgeSetupEvents {
    }

    public static class CSModSetupEvents {
 
        @SubscribeEvent
        public static void onRegistryCreatingEvent(NewRegistryEvent event) {
            event.create(new RegistryBuilder<CSVisualType>().setName(CSVisualTypes.VISUALS_KEY.location()).disableSaving());
        }

        @SubscribeEvent
        public static void onFMLCommonSetupEvent(FMLCommonSetupEvent event) {
            CSCapabilities.registerCapabilities();

            event.enqueueWork(() -> {
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(Items.PHANTOM_MEMBRANE), Ingredient.of(CSItems.LUNAR_SCRAP.get()), new ItemStack(CSItems.STARSTRUCK_SCRAP.get())));
                BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(Items.PHANTOM_MEMBRANE), Ingredient.of(Items.FEATHER), new ItemStack(CSItems.STARSTRUCK_FEATHER.get())));
            });
        }

        @SubscribeEvent
        public static void onEntityAttributeCreationEvent(EntityAttributeCreationEvent event) {
            event.put(CSEntityTypes.TEMPEST.get(), TempestBoss.createAttributes().build());
        }

        @SubscribeEvent
        public static void onGatherDataEvent(final GatherDataEvent event) {
            DataGenerator dataGenerator = event.getGenerator();
            final ExistingFileHelper efh = event.getExistingFileHelper();
            final PackOutput output = event.getGenerator().getPackOutput();
            final CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

            dataGenerator.addProvider(event.includeServer(), new CSBlockstateProvider(output, efh));
            dataGenerator.addProvider(event.includeServer(), new CSItemModelProvider(output, efh));
            dataGenerator.addProvider(event.includeServer(), new CSRecipeProvider(output));
            dataGenerator.addProvider(event.includeServer(), new CSAdvancementProvider(output, lookup, efh));

            CSTagsProvider.BlockHandler blockTagProvider = new CSTagsProvider.BlockHandler(output, lookup, efh);
            dataGenerator.addProvider(event.includeServer(), blockTagProvider);
            dataGenerator.addProvider(event.includeServer(), new CSTagsProvider.ItemHandler(output, lookup, blockTagProvider.contentsGetter(), efh));

            otherProviders(output, lookup, efh).forEach(provider -> dataGenerator.addProvider(event.includeServer(), provider));
        }
        //TODO: Move to datagen -> [ Loot Tables, Configured Feature, Placed Feature, Structure, Structure Set ]
        public static List<DataProvider> otherProviders(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper efh) {
            RegistrySetBuilder builder = new RegistrySetBuilder()
                    .add(Registries.DAMAGE_TYPE, CSDamageTypeProvider::bootstrap)
                    ;
            return List.of(
                    new DatapackBuiltinEntriesProvider(output, lookup, builder, Set.of(Celestisynth.MODID)),
                    new CSTagsProvider.DamageTypeHandler(output, lookup.thenApply(provider -> append(provider, builder)), efh)
            );
        }

        private static HolderLookup.Provider append(HolderLookup.Provider provider, RegistrySetBuilder builder) {
            return builder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), provider);
        }
    }
}
