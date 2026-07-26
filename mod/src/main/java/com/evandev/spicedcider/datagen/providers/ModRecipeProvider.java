package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.recipe.RenameRecipe;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.GRAPPLING_HOOK.get())
                .pattern(" C")
                .pattern("CC")
                .define('C', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.STICKY_GRAPPLING_HOOK.get())
                .requires(ModItems.GRAPPLING_HOOK.get())
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_grappling_hook", has(ModItems.GRAPPLING_HOOK.get()))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.FIRE_STRIKER.get())
                .requires(Items.FLINT, 2)
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(output);

        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.CAST_IRON_INGOT.get(), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CAST_IRON_BLOCK.get());
        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.CAST_IRON_NUGGET.get(), RecipeCategory.MISC, ModItems.CAST_IRON_INGOT.get(), "spicedcider:cast_iron_ingot_from_nuggets", null, "spicedcider:cast_iron_nugget_from_ingot", null);

        SpecialRecipeBuilder.special(RenameRecipe::new)
                .save(output, "spicedcider:rename_item");
    }
}