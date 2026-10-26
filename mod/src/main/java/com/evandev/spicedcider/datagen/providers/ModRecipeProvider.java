package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.architectspalette.ArchitectsPaletteCompat;
import com.evandev.spicedcider.compat.infernalexp.InfernalExpansionCompat;
import com.evandev.spicedcider.compat.tide.TideCompat;
import com.evandev.spicedcider.recipe.RenameRecipe;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
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
                .define('C', Tags.Items.INGOTS_COPPER)
                .unlockedBy("has_copper_ingot", has(Tags.Items.INGOTS_COPPER))
                .save(tideOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.STICKY_GRAPPLING_HOOK.get())
                .requires(ModItems.GRAPPLING_HOOK.get())
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_grappling_hook", has(ModItems.GRAPPLING_HOOK.get()))
                .save(tideOutput);

        TagKey<Item> adamantiteIngots = commonTag("ingots/adamantite");
        RecipeOutput adamantiteOutput = tideOutput.withConditions(new NotCondition(new TagEmptyCondition(adamantiteIngots.location())));

        hook(tideOutput, ModItems.FLUX_HOOK.get(), Ingredient.of(Tags.Items.DUSTS_REDSTONE), commonTag("nuggets/bronze"));
        hook(tideOutput, ModItems.GOLD_HOOK.get(), Ingredient.of(Tags.Items.INGOTS_GOLD), Tags.Items.NUGGETS_GOLD);
        hook(tideOutput, ModItems.HEAVY_HOOK.get(), Ingredient.of(commonTag("ingots/lead")), commonTag("nuggets/steel"));
        hook(tideOutput, ModItems.LIGHT_HOOK.get(), Ingredient.of(commonTag("ingots/rose_gold")), commonTag("nuggets/rose_gold"));
        hook(adamantiteOutput, ModItems.ADAMANTITE_HOOK.get(), Ingredient.of(adamantiteIngots), commonTag("nuggets/adamantite"));
        hook(tideOutput, ModItems.PRISMARITE_HOOK.get(), Ingredient.of(commonTag("ingots/prismarite")), commonTag("nuggets/prismarite"));
        hook(tideOutput, ModItems.FEATHERLIGHT_HOOK.get(), Ingredient.of(Items.FEATHER), commonTag("nuggets/valkyrium"));
        hook(tideOutput, ModItems.NECROMIUM_HOOK.get(), Ingredient.of(commonTag("ingots/necromium")), commonTag("nuggets/necromium"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.DOUBLE_HOOK.get())
                .requires(ModItems.LIGHT_HOOK.get())
                .requires(ModItems.HEAVY_HOOK.get())
                .requires(Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(tideOutput);

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Tags.Items.STRINGS),
                        Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("alloyed", "steel_fishing_rod"))),
                        Ingredient.of(adamantiteIngots),
                        RecipeCategory.TOOLS, ModItems.ADAMANTITE_FISHING_ROD.get())
                .unlocks("has_adamantite_ingot", has(adamantiteIngots))
                .save(adamantiteOutput.withConditions(new ModLoadedCondition("alloyed")), ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "adamantite_fishing_rod_smithing"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.FIRE_STRIKER.get())
                .requires(Items.FLINT, 2)
                .unlockedBy("has_flint", has(Items.FLINT))
                .save(output);

        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.CAST_IRON_INGOT.get(), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CAST_IRON_BLOCK.get());
        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.CAST_IRON_NUGGET.get(), RecipeCategory.MISC, ModItems.CAST_IRON_INGOT.get(), "spicedcider:cast_iron_ingot_from_nuggets", null, "spicedcider:cast_iron_nugget_from_ingot", null);

        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.TALLOW.get(), RecipeCategory.BUILDING_BLOCKS, ModBlocks.TALLOW_BLOCK.get());
        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.WORM.get(), RecipeCategory.DECORATIONS, ModBlocks.WORM_CRATE.get(), "spicedcider:worm_crate", null, "spicedcider:worm_from_crate", null);
        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModItems.GOLDEN_WORM.get(), RecipeCategory.DECORATIONS, ModBlocks.GOLDEN_WORM_CRATE.get(), "spicedcider:golden_worm_crate", null, "spicedcider:golden_worm_from_crate", null);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GOLDEN_WORM.get(), 2)
                .requires(ModItems.WORM.get(), 2)
                .requires(Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_worm", has(ModItems.WORM.get()))
                .save(output);

        woodFromLogs(output, ModBlocks.CACAO_WOOD.get(), ModBlocks.CACAO_LOG.get());
        woodFromLogs(output, ModBlocks.STRIPPED_CACAO_WOOD.get(), ModBlocks.STRIPPED_CACAO_LOG.get());

        Block deepsilt = ModBlocks.DEEPSILT.get();
        Block driedDeepsilt = ModBlocks.DRIED_DEEPSILT.get();
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, driedDeepsilt)
                .pattern("##")
                .pattern("##")
                .define('#', deepsilt)
                .unlockedBy(getHasName(deepsilt), has(deepsilt))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_DRIED_DEEPSILT.get(), 4)
                .pattern("##")
                .pattern("##")
                .define('#', driedDeepsilt)
                .unlockedBy(getHasName(driedDeepsilt), has(driedDeepsilt))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIED_DEEPSILT.get(), 4)
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .define('#', driedDeepsilt)
                .unlockedBy(getHasName(driedDeepsilt), has(driedDeepsilt))
                .save(output);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(driedDeepsilt), RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_DRIED_DEEPSILT.get(), 0.1F, 200)
                .unlockedBy(getHasName(driedDeepsilt), has(driedDeepsilt))
                .save(output);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_DRIED_DEEPSILT.get(), driedDeepsilt, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_DRIED_DEEPSILT.get(), driedDeepsilt, 1);

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
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_GLOWSTONE_BRICKS.get(), 4)
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .define('#', glowstoneBricks)
                .unlockedBy(getHasName(glowstoneBricks), has(glowstoneBricks))
                .save(output);
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

        if (ModItems.TWISTED_SIGN != null) {
            RecipeOutput paletteOutput = output.withConditions(new ModLoadedCondition(ArchitectsPaletteCompat.MOD_ID));
            Block planks = ArchitectsPaletteCompat.twistedPlanks();
            Block strippedLog = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ArchitectsPaletteCompat.MOD_ID, "stripped_twisted_log"));
            signBuilder(ModItems.TWISTED_SIGN.get(), Ingredient.of(planks))
                    .unlockedBy(getHasName(planks), has(planks))
                    .save(paletteOutput);
            hangingSign(paletteOutput, ModItems.TWISTED_HANGING_SIGN.get(), strippedLog);
            woodenBoat(paletteOutput, ModItems.TWISTED_BOAT.get(), planks);
            chestBoat(paletteOutput, ModItems.TWISTED_CHEST_BOAT.get(), ModItems.TWISTED_BOAT.get());
        }

        for (DyeColor color : DyeColor.values()) {
            Item balloon = ModItems.BALLOONS.get(color).get();
            Ingredient others = Ingredient.of(ModItems.BALLOONS.entrySet().stream()
                    .filter(entry -> entry.getKey() != color)
                    .map(entry -> new ItemStack(entry.getValue().get())));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, balloon)
                    .requires(color.getTag())
                    .requires(others)
                    .group("balloon_dye")
                    .unlockedBy("has_balloon", has(ModTags.Items.BALLOONS))
                    .save(output, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "dye_" + color.getName() + "_balloon"));
        }

        SpecialRecipeBuilder.special(RenameRecipe::new)
                .save(output, "spicedcider:rename_item");
    }

    private static void stonecutting(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike base, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), category, result, count)
                .unlockedBy(getHasName(base), has(base))
                .save(output, SpicedCider.MOD_ID + ":" + getConversionRecipeName(result, base) + "_stonecutting");
    }

    private static TagKey<Item> commonTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static void hook(RecipeOutput output, ItemLike hook, Ingredient tip, TagKey<Item> nuggets) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hook)
                .pattern(" T")
                .pattern(" N")
                .pattern("NN")
                .define('T', tip)
                .define('N', nuggets)
                .unlockedBy("has_nugget", has(nuggets))
                .save(output);
    }
}
