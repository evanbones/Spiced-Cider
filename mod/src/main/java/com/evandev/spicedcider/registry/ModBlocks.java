package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.architectspalette.ArchitectsPaletteCompat;
import com.evandev.spicedcider.compat.bountifulfares.BountifulFaresCompat;
import com.evandev.spicedcider.compat.infernalexp.InfernalExpansionCompat;
import com.evandev.spicedcider.content.block.AlgalBlock;
import com.evandev.spicedcider.content.block.BuddingPrismarineBlock;
import com.evandev.spicedcider.content.block.CacaoPodBlock;
import com.evandev.spicedcider.content.block.FruitLeavesBlock;
import com.evandev.spicedcider.content.block.FruitLogBlock;
import com.evandev.spicedcider.content.block.HangingFruitBlock;
import com.evandev.spicedcider.content.block.FluidCauldronBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.registries.BuiltInRegistries;
import com.evandev.spicedcider.content.block.LatexCauldronBlock;
import com.evandev.spicedcider.content.block.PrismarineClusterBlock;
import com.evandev.spicedcider.content.block.TallUnderwaterBrambleBlock;
import com.evandev.spicedcider.content.block.TemporaryCobwebBlock;
import com.evandev.spicedcider.content.block.TubewormBlock;
import com.evandev.spicedcider.content.block.UnderwaterBrambleBlock;
import com.evandev.spicedcider.content.block.VentBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpicedCider.MOD_ID);

    public static final DeferredBlock<Block> TEMPORARY_COBWEB = BLOCKS.register("temporary_cobweb",
            () -> new TemporaryCobwebBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBWEB)));

    public static final DeferredBlock<Block> CAST_IRON_BLOCK = BLOCKS.register("cast_iron_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<Block> TALLOW_BLOCK = BLOCKS.register("tallow_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEYCOMB_BLOCK)));

    public static final DeferredBlock<LiquidBlock> LATEX = BLOCKS.register("latex",
            () -> new LiquidBlock(ModFluids.LATEX.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_LIGHT_GRAY)));

    public static final DeferredBlock<LiquidBlock> RESIN = BLOCKS.register("resin",
            () -> new LiquidBlock(ModFluids.RESIN.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<LiquidBlock> MAPLE_SYRUP = BLOCKS.register("maple_syrup",
            () -> new LiquidBlock(ModFluids.MAPLE_SYRUP.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_ORANGE)));

    public static final DeferredBlock<LiquidBlock> RESIN_OIL = BLOCKS.register("resin_oil",
            () -> new LiquidBlock(ModFluids.RESIN_OIL.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_BROWN)));

    public static final DeferredBlock<FluidCauldronBlock> CHOCOLATE_CAULDRON = BLOCKS.register("chocolate_cauldron",
            () -> new FluidCauldronBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON), "chocolate", () -> fluid("create", "chocolate"), null));
    public static final DeferredBlock<FluidCauldronBlock> SPRING_WATER_CAULDRON = BLOCKS.register("spring_water_cauldron",
            () -> new FluidCauldronBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON), "spring_water", () -> fluid("spelunkery", "spring_water"), null));
    public static final DeferredBlock<FluidCauldronBlock> LUMISENE_CAULDRON = BLOCKS.register("lumisene_cauldron",
            () -> new FluidCauldronBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON).lightLevel(state -> 2 + state.getValue(FluidCauldronBlock.LEVEL) * 2),
                    "lumisene", () -> fluid("supplementaries", "lumisene"), () -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("supplementaries", "lumisene_bottle"))));

    public static final DeferredBlock<Block> LATEX_CAULDRON = BLOCKS.register("latex_cauldron",
            () -> new LatexCauldronBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON)));

    public static final DeferredBlock<Block> WORMY_DIRT = BLOCKS.register("wormy_dirt",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)));

    public static final DeferredBlock<Block> DEPOSIT = BLOCKS.register("deposit",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(3.0F).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DEEPSLATE_DEPOSIT = BLOCKS.register("deepslate_deposit",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> BLACKSTONE_DEPOSIT = BLOCKS.register("blackstone_deposit",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACKSTONE).strength(3.0F, 6.0F).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> WORM_CRATE = BLOCKS.register("worm_crate",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)));

    public static final DeferredBlock<Block> GOLDEN_WORM_CRATE = BLOCKS.register("golden_worm_crate",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)));

    @Nullable
    public static final DeferredBlock<Block> ORANGE_CRATE = fruitCrate("orange_crate");
    @Nullable
    public static final DeferredBlock<Block> PLUM_CRATE = fruitCrate("plum_crate");
    @Nullable
    public static final DeferredBlock<Block> HOARY_APPLE_CRATE = fruitCrate("hoary_apple_crate");

    public static final DeferredBlock<SaplingBlock> GLOAMWOOD_SAPLING = BLOCKS.register("gloamwood_sapling",
            () -> new SaplingBlock(new TreeGrower("gloamwood", Optional.empty(), Optional.of(ModTreeFeatures.GLOAMWOOD), Optional.empty()),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING).mapColor(MapColor.PLANT)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_GLOAMWOOD_SAPLING = BLOCKS.register("potted_gloamwood_sapling",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GLOAMWOOD_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING)));

    public static final DeferredBlock<Block> STRIPPED_CACAO_LOG = BLOCKS.register("stripped_cacao_log",
            () -> new FruitLogBlock(() -> null, fruitLogProperties(MapColor.COLOR_PINK)));
    public static final DeferredBlock<Block> STRIPPED_CACAO_WOOD = BLOCKS.register("stripped_cacao_wood",
            () -> new FruitLogBlock(() -> null, fruitLogProperties(MapColor.COLOR_PINK)));
    public static final DeferredBlock<Block> CACAO_LOG = BLOCKS.register("cacao_log",
            () -> new FruitLogBlock(STRIPPED_CACAO_LOG, fruitLogProperties(MapColor.TERRACOTTA_BROWN)));
    public static final DeferredBlock<Block> CACAO_WOOD = BLOCKS.register("cacao_wood",
            () -> new FruitLogBlock(STRIPPED_CACAO_WOOD, fruitLogProperties(MapColor.TERRACOTTA_BROWN)));
    public static final DeferredBlock<Block> CACAO_LEAVES = BLOCKS.register("cacao_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LEAVES)));
    public static final DeferredBlock<Block> FLOWERING_CACAO_LEAVES = BLOCKS.register("flowering_cacao_leaves",
            () -> new FruitLeavesBlock(ModBlocks.CACAO_POD, BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LEAVES)));
    public static final DeferredBlock<Block> CACAO_POD = BLOCKS.register("cacao_pod",
            () -> new CacaoPodBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COCOA)));
    public static final DeferredBlock<SaplingBlock> CACAO_SAPLING = BLOCKS.register("cacao_sapling",
            () -> new SaplingBlock(new TreeGrower("cacao", Optional.empty(), Optional.of(ModTreeFeatures.CACAO), Optional.empty()),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_SAPLING)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_CACAO_SAPLING = BLOCKS.register("potted_cacao_sapling",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CACAO_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_JUNGLE_SAPLING)));

    public static final DeferredBlock<Block> STRIPPED_PEAR_LOG = BLOCKS.register("stripped_pear_log",
            () -> new FruitLogBlock(() -> null, fruitLogProperties(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<Block> STRIPPED_PEAR_WOOD = BLOCKS.register("stripped_pear_wood",
            () -> new FruitLogBlock(() -> null, fruitLogProperties(MapColor.TERRACOTTA_PINK)));
    public static final DeferredBlock<Block> PEAR_LOG = BLOCKS.register("pear_log",
            () -> new FruitLogBlock(STRIPPED_PEAR_LOG, fruitLogProperties(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<Block> PEAR_WOOD = BLOCKS.register("pear_wood",
            () -> new FruitLogBlock(STRIPPED_PEAR_WOOD, fruitLogProperties(MapColor.TERRACOTTA_GRAY)));
    public static final DeferredBlock<Block> PEAR_LEAVES = BLOCKS.register("pear_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<Block> FLOWERING_PEAR_LEAVES = BLOCKS.register("flowering_pear_leaves",
            () -> new FruitLeavesBlock(ModBlocks.HANGING_PEAR, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<Block> HANGING_PEAR = BLOCKS.register("hanging_pear",
            () -> new HangingFruitBlock(ModTags.Blocks.PEAR_LEAVES, ResourceLocation.fromNamespaceAndPath("nomansland", "pear"), BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).mapColor(MapColor.PLANT).sound(SoundType.AZALEA)));
    public static final DeferredBlock<SaplingBlock> PEAR_SAPLING = BLOCKS.register("pear_sapling",
            () -> new SaplingBlock(new TreeGrower("pear", Optional.empty(), Optional.of(ModTreeFeatures.PEAR), Optional.empty()),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final DeferredBlock<FlowerPotBlock> POTTED_PEAR_SAPLING = BLOCKS.register("potted_pear_sapling",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, PEAR_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING)));

    public static final DeferredBlock<Block> STRIPPED_AVOCADO_LOG = BLOCKS.register("stripped_avocado_log",
            () -> new FruitLogBlock(() -> null, fruitLogProperties(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> STRIPPED_AVOCADO_WOOD = BLOCKS.register("stripped_avocado_wood",
            () -> new FruitLogBlock(() -> null, fruitLogProperties(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> AVOCADO_LOG = BLOCKS.register("avocado_log",
            () -> new FruitLogBlock(STRIPPED_AVOCADO_LOG, fruitLogProperties(MapColor.PODZOL)));
    public static final DeferredBlock<Block> AVOCADO_WOOD = BLOCKS.register("avocado_wood",
            () -> new FruitLogBlock(STRIPPED_AVOCADO_WOOD, fruitLogProperties(MapColor.PODZOL)));
    public static final DeferredBlock<Block> FLOWERING_AVOCADO_LEAVES = BLOCKS.register("flowering_avocado_leaves",
            () -> new FruitLeavesBlock(ModBlocks.HANGING_AVOCADO, BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LEAVES)));
    public static final DeferredBlock<Block> HANGING_AVOCADO = BLOCKS.register("hanging_avocado",
            () -> new HangingFruitBlock(ModTags.Blocks.AVOCADO_LEAVES, ResourceLocation.fromNamespaceAndPath("culturaldelights", "avocado"),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).mapColor(MapColor.PLANT).sound(SoundType.AZALEA)));

    public static final DeferredBlock<Block> DEEPSILT = BLOCKS.register("deepsilt",
            () -> new ColoredFallingBlock(new ColorRGBA(0x3a3a3a), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_BLACK).sound(ModSounds.SILT)));
    public static final DeferredBlock<Block> DRIED_DEEPSILT = BLOCKS.register("dried_deepsilt", () -> new Block(driedDeepsiltProperties()));
    public static final DeferredBlock<Block> CUT_DRIED_DEEPSILT = BLOCKS.register("cut_dried_deepsilt", () -> new Block(driedDeepsiltProperties()));
    public static final DeferredBlock<Block> POLISHED_DRIED_DEEPSILT = BLOCKS.register("polished_dried_deepsilt", () -> new Block(driedDeepsiltProperties()));
    public static final DeferredBlock<Block> CHISELED_DRIED_DEEPSILT = BLOCKS.register("chiseled_dried_deepsilt", () -> new Block(driedDeepsiltProperties()));

    public static final DeferredBlock<Block> ALGAE = BLOCKS.register("algae",
            () -> new AlgalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK).mapColor(MapColor.COLOR_GREEN).sound(ModSounds.ALGAE), Blocks.DIRT));
    public static final DeferredBlock<Block> ALGAL_SAND = BLOCKS.register("algal_sand",
            () -> new AlgalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_LIGHT_GREEN).sound(ModSounds.ALGAL_SAND), Blocks.SAND));
    public static final DeferredBlock<Block> ALGAL_GRAVEL = BLOCKS.register("algal_gravel",
            () -> new AlgalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).mapColor(MapColor.COLOR_LIGHT_GREEN).sound(ModSounds.ALGAL_GRAVEL), Blocks.GRAVEL));

    public static final DeferredBlock<Block> ACHROMARINE = BLOCKS.register("achromarine",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE)));
    public static final DeferredBlock<Block> CHROMARINE = BLOCKS.register("chromarine",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE).sound(ModSounds.CHROMARINE)));
    public static final DeferredBlock<Block> BUDDING_PRISMARINE = BLOCKS.register("budding_prismarine",
            () -> new BuddingPrismarineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BUDDING_AMETHYST).mapColor(MapColor.COLOR_CYAN).sound(SoundType.STONE).randomTicks()));
    public static final DeferredBlock<Block> SMALL_PRISMARINE_BUD = BLOCKS.register("small_prismarine_bud",
            () -> new PrismarineClusterBlock(3, 4, BlockBehaviour.Properties.ofFullCopy(Blocks.SMALL_AMETHYST_BUD).mapColor(MapColor.COLOR_CYAN).sound(ModSounds.PRISMARINE_CLUSTER).lightLevel(state -> 0)));
    public static final DeferredBlock<Block> MEDIUM_PRISMARINE_BUD = BLOCKS.register("medium_prismarine_bud",
            () -> new PrismarineClusterBlock(4, 3, BlockBehaviour.Properties.ofFullCopy(Blocks.MEDIUM_AMETHYST_BUD).mapColor(MapColor.COLOR_CYAN).sound(ModSounds.PRISMARINE_CLUSTER).lightLevel(state -> 0)));
    public static final DeferredBlock<Block> LARGE_PRISMARINE_BUD = BLOCKS.register("large_prismarine_bud",
            () -> new PrismarineClusterBlock(5, 3, BlockBehaviour.Properties.ofFullCopy(Blocks.LARGE_AMETHYST_BUD).mapColor(MapColor.COLOR_CYAN).sound(ModSounds.PRISMARINE_CLUSTER).lightLevel(state -> 0)));
    public static final DeferredBlock<Block> PRISMARINE_CLUSTER = BLOCKS.register("prismarine_cluster",
            () -> new PrismarineClusterBlock(7, 3, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_CYAN).sound(ModSounds.PRISMARINE_CLUSTER).lightLevel(state -> 0)));

    public static final DeferredBlock<Block> SULFIDE = BLOCKS.register("sulfide",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE).mapColor(MapColor.TERRACOTTA_BLACK)));
    public static final DeferredBlock<Block> SULFIDE_VENT = BLOCKS.register("sulfide_vent",
            () -> new VentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE).mapColor(MapColor.TERRACOTTA_BLACK).noOcclusion(), ModParticleTypes.BLACK_VENT_SMOKE));
    public static final DeferredBlock<Block> CALCITE_VENT = BLOCKS.register("calcite_vent",
            () -> new VentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE).noOcclusion(), ModParticleTypes.WHITE_VENT_SMOKE));
    public static final DeferredBlock<Block> TUBEWORM = BLOCKS.register("tubeworm",
            () -> new TubewormBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUBE_CORAL_BLOCK).mapColor(MapColor.COLOR_RED).noOcclusion().strength(0.5F).sound(SoundType.CORAL_BLOCK)));

    public static final DeferredBlock<Block> CHROMATIC_BRAMBLE = BLOCKS.register("chromatic_bramble",
            () -> new UnderwaterBrambleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEAD_BUSH).mapColor(MapColor.COLOR_PURPLE).sound(SoundType.WET_GRASS)));
    public static final DeferredBlock<Block> TALL_CHROMATIC_BRAMBLE = BLOCKS.register("tall_chromatic_bramble",
            () -> new TallUnderwaterBrambleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS).mapColor(MapColor.COLOR_PURPLE).sound(SoundType.WET_GRASS)));
    public static final DeferredBlock<Block> ACHROMATIC_BRAMBLE = BLOCKS.register("achromatic_bramble",
            () -> new UnderwaterBrambleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEAD_BUSH).mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WET_GRASS)));
    public static final DeferredBlock<Block> TALL_ACHROMATIC_BRAMBLE = BLOCKS.register("tall_achromatic_bramble",
            () -> new TallUnderwaterBrambleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS).mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.WET_GRASS)));

    public static final DeferredBlock<Block> GLOWSTONE_BRICKS = BLOCKS.register("glowstone_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE)));
    public static final DeferredBlock<StairBlock> GLOWSTONE_BRICK_STAIRS = BLOCKS.register("glowstone_brick_stairs",
            () -> new StairBlock(GLOWSTONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE)));
    public static final DeferredBlock<SlabBlock> GLOWSTONE_BRICK_SLAB = BLOCKS.register("glowstone_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE)));
    public static final DeferredBlock<WallBlock> GLOWSTONE_BRICK_WALL = BLOCKS.register("glowstone_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE).forceSolidOn()));
    public static final DeferredBlock<Block> CHISELED_GLOWSTONE_BRICKS = BLOCKS.register("chiseled_glowstone_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE)));
    public static final DeferredBlock<RotatedPillarBlock> POLISHED_GLOWSTONE_PILLAR = BLOCKS.register("polished_glowstone_pillar",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLOWSTONE)));

    @Nullable
    public static final DeferredBlock<StairBlock> POLISHED_GLOWSTONE_STAIRS = InfernalExpansionCompat.isLoaded()
            ? BLOCKS.register("polished_glowstone_stairs", () -> new StairBlock(InfernalExpansionCompat.polishedGlowstone().defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(InfernalExpansionCompat.polishedGlowstone())))
            : null;
    @Nullable
    public static final DeferredBlock<SlabBlock> POLISHED_GLOWSTONE_SLAB = InfernalExpansionCompat.isLoaded()
            ? BLOCKS.register("polished_glowstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(InfernalExpansionCompat.polishedGlowstone())))
            : null;
    @Nullable
    public static final DeferredBlock<WallBlock> POLISHED_GLOWSTONE_WALL = InfernalExpansionCompat.isLoaded()
            ? BLOCKS.register("polished_glowstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(InfernalExpansionCompat.polishedGlowstone()).forceSolidOn()))
            : null;

    @Nullable
    public static final DeferredBlock<StandingSignBlock> TWISTED_SIGN = ArchitectsPaletteCompat.isLoaded()
            ? BLOCKS.register("twisted_sign", () -> new StandingSignBlock(ModWoodTypes.TWISTED,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).mapColor(MapColor.COLOR_PURPLE)))
            : null;
    @Nullable
    public static final DeferredBlock<WallSignBlock> TWISTED_WALL_SIGN = ArchitectsPaletteCompat.isLoaded()
            ? BLOCKS.register("twisted_wall_sign", () -> new WallSignBlock(ModWoodTypes.TWISTED,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(MapColor.COLOR_PURPLE).dropsLike(TWISTED_SIGN.get())))
            : null;
    @Nullable
    public static final DeferredBlock<CeilingHangingSignBlock> TWISTED_HANGING_SIGN = ArchitectsPaletteCompat.isLoaded()
            ? BLOCKS.register("twisted_hanging_sign", () -> new CeilingHangingSignBlock(ModWoodTypes.TWISTED,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(MapColor.COLOR_PURPLE)))
            : null;
    @Nullable
    public static final DeferredBlock<WallHangingSignBlock> TWISTED_WALL_HANGING_SIGN = ArchitectsPaletteCompat.isLoaded()
            ? BLOCKS.register("twisted_wall_hanging_sign", () -> new WallHangingSignBlock(ModWoodTypes.TWISTED,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(MapColor.COLOR_PURPLE).dropsLike(TWISTED_HANGING_SIGN.get())))
            : null;

    private static BlockBehaviour.Properties fruitLogProperties(MapColor color) {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LOG).mapColor(color).noOcclusion().isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false);
    }

    private static BlockBehaviour.Properties driedDeepsiltProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).mapColor(MapColor.COLOR_BLACK).sound(ModSounds.DRIED_SILT);
    }

    private static Fluid fluid(String namespace, String path) {
        return BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    @Nullable
    private static DeferredBlock<Block> fruitCrate(String name) {
        return BountifulFaresCompat.isLoaded()
                ? BLOCKS.register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)))
                : null;
    }
}
