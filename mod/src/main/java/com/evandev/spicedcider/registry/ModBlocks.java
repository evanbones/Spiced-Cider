package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.infernalexp.InfernalExpansionCompat;
import com.evandev.spicedcider.content.block.TemporaryCobwebBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpicedCider.MOD_ID);

    public static final DeferredBlock<Block> TEMPORARY_COBWEB = BLOCKS.register("temporary_cobweb",
            () -> new TemporaryCobwebBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBWEB)));

    public static final DeferredBlock<Block> CAST_IRON_BLOCK = BLOCKS.register("cast_iron_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<Block> TALLOW_BLOCK = BLOCKS.register("tallow_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEYCOMB_BLOCK)));

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

    public static final Map<DyeColor, DeferredBlock<StairBlock>> WOOL_STAIRS = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, DeferredBlock<SlabBlock>> WOOL_SLABS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            WOOL_STAIRS.put(color, BLOCKS.register(color.getName() + "_wool_stairs",
                    () -> new StairBlock(wool(color).defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(wool(color)))));
            WOOL_SLABS.put(color, BLOCKS.register(color.getName() + "_wool_slab",
                    () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(wool(color)))));
        }
    }

    public static Block wool(DyeColor color) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.withDefaultNamespace(color.getName() + "_wool"));
    }
}
