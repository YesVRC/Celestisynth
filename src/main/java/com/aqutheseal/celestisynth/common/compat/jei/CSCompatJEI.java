package com.aqutheseal.celestisynth.common.compat.jei;

import com.aqutheseal.celestisynth.Celestisynth;
import com.aqutheseal.celestisynth.client.gui.celestialcrafting.CelestialCraftingMenu;
import com.aqutheseal.celestisynth.client.gui.celestialcrafting.CelestialCraftingScreen;
import com.aqutheseal.celestisynth.client.gui.starlitfactory.StarlitFactoryMenu;
import com.aqutheseal.celestisynth.client.gui.starlitfactory.StarlitFactoryScreen;
import com.aqutheseal.celestisynth.common.compat.jei.guihandler.StarlitFactoryGuiHandler;
import com.aqutheseal.celestisynth.common.compat.jei.recipecategory.CelestialCraftingRecipeCategory;
import com.aqutheseal.celestisynth.common.compat.jei.recipecategory.StarlitFactoryRecipeCategory;
import com.aqutheseal.celestisynth.common.recipe.StarlitFactoryRecipe;
import com.aqutheseal.celestisynth.common.recipe.celestialcrafting.CelestialCraftingRecipe;
import com.aqutheseal.celestisynth.common.registry.CSBlocks;
import com.aqutheseal.celestisynth.common.registry.CSMenuTypes;
import com.aqutheseal.celestisynth.common.registry.CSRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.library.plugins.vanilla.RecipeBookGuiHandler;
import mezz.jei.library.plugins.vanilla.crafting.CategoryRecipeValidator;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class CSCompatJEI implements IModPlugin {
    public static final RecipeType<CelestialCraftingRecipe> CELESTIAL_CRAFTING = RecipeType.create(Celestisynth.MODID, "celestisynth", CelestialCraftingRecipe.class);
    public static final RecipeType<StarlitFactoryRecipe> STARLIT_FACTORY = RecipeType.create(Celestisynth.MODID, "starlit_factory", StarlitFactoryRecipe.class);
    private static final ResourceLocation ID = Celestisynth.prefix("plugin");

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CelestialCraftingRecipeCategory(helper));
        registration.addRecipeCategories(new StarlitFactoryRecipeCategory(helper));
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(CelestialCraftingScreen.class, new RecipeBookGuiHandler<>());
        registration.addGuiContainerHandler(StarlitFactoryScreen.class, new StarlitFactoryGuiHandler());
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CelestialCraftingMenu.class, CSMenuTypes.CELESTIAL_CRAFTING.get(), CELESTIAL_CRAFTING, 1, 8, 9, 36);
        registration.addRecipeTransferHandler(StarlitFactoryMenu.class, CSMenuTypes.STARLIT_FACTORY.get(), STARLIT_FACTORY, 0, 7, 8, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(CSBlocks.CELESTIAL_CRAFTING_TABLE.get()), CELESTIAL_CRAFTING);
        registration.addRecipeCatalyst(new ItemStack(CSBlocks.STARLIT_FACTORY.get()), STARLIT_FACTORY);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        CategoryRecipeValidator<CelestialCraftingRecipe> validator = new CategoryRecipeValidator<>(new CelestialCraftingRecipeCategory(helper), registration.getIngredientManager(), 9);
        List<CelestialCraftingRecipe> craftingRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(CSRecipeTypes.CELESTIAL_CRAFTING_TYPE.get());
        List<StarlitFactoryRecipe> starlitFactoryRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(CSRecipeTypes.STARLIT_FACTORY_TYPE.get());
        registration.addRecipes(CELESTIAL_CRAFTING, craftingRecipes);
        registration.addRecipes(STARLIT_FACTORY, starlitFactoryRecipes);
    }
}
