package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.infernalexp.InfernalExpansionCompat;
import com.evandev.spicedcider.compat.tide.TideCompat;
import com.evandev.spicedcider.recipe.RenameRecipe;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        RecipeOutput tideOutput = output.withConditions(new ModLoadedCondition(TideCompat.MOD_ID));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.GRAPPLING_HOOK.get())
                .pattern(" C")
                .pattern("CC")
                .define('C', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(tideOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.STICKY_GRAPPLING_HOOK.get())
                .requires(ModItems.GRAPPLING_HOOK.get())
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_grappling_hook", has(ModItems.GRAPPLING_HOOK.get()))
                .save(tideOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.FIRE_STRIKER.get())
                .requires(Items.FLINT, 2)
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(output);

        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.CAST_IRON_INGOT.get(), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CAST_IRON_BLOCK.get());
        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.CAST_IRON_NUGGET.get(), RecipeCategory.MISC, ModItems.CAST_IRON_INGOT.get(), "spicedcider:cast_iron_ingot_from_nuggets", null, "spicedcider:cast_iron_nugget_from_ingot", null);

        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.TALLOW.get(), RecipeCategory.BUILDING_BLOCKS, ModBlocks.TALLOW_BLOCK.get());

        Block glowstoneBricks = ModBlocks.GLOWSTONE_BRICKS.get();
        Block glowstoneBrickStairs = ModBlocks.GLOWSTONE_BRICK_STAIRS.get();
        Block glowstoneBrickSlab = ModBlocks.GLOWSTONE_BRICK_SLAB.get();
        Block glowstoneBrickWall = ModBlocks.GLOWSTONE_BRICK_WALL.get();
        Block glowstonePillar = ModBlocks.POLISHED_GLOWSTONE_PILLAR.get();
        stairBuilder(glowstoneBrickStairs, Ingredient.of(glowstoneBricks))
                .unlockedBy(getHasName(glowstoneBricks), has(glowstoneBricks))
                .save(output);
        slab(output, RecipeCategory.BUILDING_BLOCKS, glowstoneBrickSlab, glowstoneBricks);
        wall(output, RecipeCategory.DECORATIONS, glowstoneBrickWall, glowstoneBricks);
        chiseled(output, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_GLOWSTONE_BRICKS.get(), glowstoneBrickSlab);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, glowstoneBrickStairs, glowstoneBricks, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, glowstoneBrickSlab, glowstoneBricks, 2);
        stonecutting(output, RecipeCategory.DECORATIONS, glowstoneBrickWall, glowstoneBricks, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_GLOWSTONE_BRICKS.get(), glowstoneBricks, 1);
        for (Block glowstoneProduct : List.of(glowstoneBricks, glowstoneBrickStairs, glowstoneBrickWall, ModBlocks.CHISELED_GLOWSTONE_BRICKS.get(), glowstonePillar)) {
            stonecutting(output, RecipeCategory.BUILDING_BLOCKS, glowstoneProduct, Blocks.GLOWSTONE, 1);
        }
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, glowstoneBrickSlab, Blocks.GLOWSTONE, 2);

        if (ModBlocks.POLISHED_GLOWSTONE_STAIRS != null) {
            RecipeOutput infernalOutput = output.withConditions(new ModLoadedCondition(InfernalExpansionCompat.MOD_ID));
            Block polished = InfernalExpansionCompat.polishedGlowstone();
            Block stairs = ModBlocks.POLISHED_GLOWSTONE_STAIRS.get();
            Block slab = ModBlocks.POLISHED_GLOWSTONE_SLAB.get();
            Block wall = ModBlocks.POLISHED_GLOWSTONE_WALL.get();
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, glowstoneBricks, 4)
                    .pattern("##")
                    .pattern("##")
                    .define('#', polished)
                    .unlockedBy(getHasName(polished), has(polished))
                    .save(infernalOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, glowstonePillar, 2)
                    .pattern("#")
                    .pattern("#")
                    .define('#', polished)
                    .unlockedBy(getHasName(polished), has(polished))
                    .save(infernalOutput);
            for (Block glowstoneProduct : List.of(glowstoneBricks, glowstoneBrickStairs, glowstoneBrickWall, ModBlocks.CHISELED_GLOWSTONE_BRICKS.get(), glowstonePillar)) {
                stonecutting(infernalOutput, RecipeCategory.BUILDING_BLOCKS, glowstoneProduct, polished, 1);
            }
            stonecutting(infernalOutput, RecipeCategory.BUILDING_BLOCKS, glowstoneBrickSlab, polished, 2);
            stairBuilder(stairs, Ingredient.of(polished))
                    .unlockedBy(getHasName(polished), has(polished))
                    .save(infernalOutput);
            slab(infernalOutput, RecipeCategory.BUILDING_BLOCKS, slab, polished);
            wall(infernalOutput, RecipeCategory.DECORATIONS, wall, polished);
            for (Block base : List.of(polished, Blocks.GLOWSTONE)) {
                stonecutting(infernalOutput, RecipeCategory.BUILDING_BLOCKS, stairs, base, 1);
                stonecutting(infernalOutput, RecipeCategory.BUILDING_BLOCKS, slab, base, 2);
                stonecutting(infernalOutput, RecipeCategory.DECORATIONS, wall, base, 1);
            }
        }

        for (DyeColor color : DyeColor.values()) {
            Block wool = ModBlocks.wool(color);
            stairBuilder(ModBlocks.WOOL_STAIRS.get(color).get(), Ingredient.of(wool))
                    .group("wool_stairs")
                    .unlockedBy(getHasName(wool), has(wool))
                    .save(output);
            slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WOOL_SLABS.get(color).get(), Ingredient.of(wool))
                    .group("wool_slab")
                    .unlockedBy(getHasName(wool), has(wool))
                    .save(output);
        }

        SpecialRecipeBuilder.special(RenameRecipe::new)
                .save(output, "spicedcider:rename_item");
    }

    private static void stonecutting(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike base, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), category, result, count)
                .unlockedBy(getHasName(base), has(base))
                .save(output, SpicedCider.MOD_ID + ":" + getConversionRecipeName(result, base) + "_stonecutting");
    }
}