package com.aqutheseal.celestisynth.common.compat.jei.recipecategory;

import com.aqutheseal.celestisynth.common.compat.jei.CSCompatJEI;
import com.aqutheseal.celestisynth.common.compat.jei.drawable.StarlitFactoryDrawable;
import com.aqutheseal.celestisynth.common.recipe.StarlitFactoryRecipe;
import com.aqutheseal.celestisynth.common.registry.CSBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class StarlitFactoryRecipeCategory implements IRecipeCategory<StarlitFactoryRecipe> {
    private final IDrawable background = new StarlitFactoryDrawable();
    private final IDrawable icon;

    public StarlitFactoryRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(CSBlocks.STARLIT_FACTORY.get()));
    }

    @Override
    public RecipeType<StarlitFactoryRecipe> getRecipeType() {
        return CSCompatJEI.STARLIT_FACTORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.celestisynth.starlit_factory");
    }

    public IDrawable getBackground() {
        return this.background;
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    public void setRecipe(IRecipeLayoutBuilder builder, StarlitFactoryRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 17).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 17).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 53).addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.INPUT, 108, 17).addIngredients(recipe.getIngredients().get(3));
        builder.addSlot(RecipeIngredientRole.INPUT, 108, 35).addIngredients(recipe.getIngredients().get(4));
        builder.addSlot(RecipeIngredientRole.INPUT, 108, 53).addIngredients(recipe.getIngredients().get(5));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 34).addItemStack(recipe.getResult());
    }

    public boolean isHandled(StarlitFactoryRecipe recipe) {
        return true;
    }
}
