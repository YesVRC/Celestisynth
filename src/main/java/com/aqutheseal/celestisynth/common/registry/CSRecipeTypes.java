package com.aqutheseal.celestisynth.common.registry;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.common.recipe.StarlitFactoryRecipe;
import com.aqutheseal.celestisynth.common.recipe.celestialcrafting.CelestialCraftingRecipe;
import com.aqutheseal.celestisynth.common.recipe.celestialcrafting.CelestialShapedRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CSRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Celestisynth.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Celestisynth.MODID);

    public static final RegistryObject<RecipeType<CelestialCraftingRecipe>> CELESTIAL_CRAFTING_TYPE = RECIPE_TYPES.register("celestial_crafting_type", () -> new SimpleNamedRecipeType<>("celestial_crafting"));
    public static final RegistryObject<RecipeSerializer<CelestialShapedRecipe>> SHAPED_CELESTIAL_CRAFTING = RECIPE_SERIALIZERS.register("shaped_celestial_crafting", CelestialShapedRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<StarlitFactoryRecipe>> STARLIT_FACTORY_TYPE = RECIPE_TYPES.register("starlit_factory_type", () -> new SimpleNamedRecipeType<>("starlit_factory"));
    public static final RegistryObject<RecipeSerializer<StarlitFactoryRecipe>> STARLIT_FACTORY = RECIPE_SERIALIZERS.register("starlit_factory", StarlitFactoryRecipe.Serializer::new);

    public record SimpleNamedRecipeType<T extends Recipe<?>>(String name) implements RecipeType<T> {
        @Override
        public String toString() {
            return name;
        }
    }
}
