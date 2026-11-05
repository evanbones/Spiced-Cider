package com.evandev.spicedcider.compat.firevariants;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.teamabnormals.buzzier_bees.common.block.SpecialCandleBlock;
import com.teamabnormals.buzzier_bees.common.block.SpecialCandleCakeBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class BuzzierBeesFireBlocks {
    public static final ResourceLocation SULFUR_FLAME = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "small_sulfur_fire_flame");
    public static final ResourceLocation GLOWLIGHT_FLAME = ResourceLocation.fromNamespaceAndPath("infernalexp", "glowstone_sparkle");

    public static DeferredBlock<Block> sulfurCandle;
    public static DeferredBlock<Block> sulfurCandleCake;
    public static DeferredItem<BlockItem> sulfurCandleItem;
    public static DeferredBlock<Block> glowlightCandle;
    public static DeferredBlock<Block> glowlightCandleCake;
    public static DeferredItem<BlockItem> glowlightCandleItem;

    public static void register(boolean glowlight) {
        sulfurCandle = candle("sulfur_candle", SULFUR_FLAME, MapColor.COLOR_YELLOW);
        sulfurCandleCake = cake("sulfur_candle_cake", sulfurCandle, SULFUR_FLAME);
        sulfurCandleItem = ModItems.ITEMS.registerSimpleBlockItem(sulfurCandle);
        if (glowlight) {
            glowlightCandle = candle("glowlight_candle", GLOWLIGHT_FLAME, MapColor.COLOR_YELLOW);
            glowlightCandleCake = cake("glowlight_candle_cake", glowlightCandle, GLOWLIGHT_FLAME);
            glowlightCandleItem = ModItems.ITEMS.registerSimpleBlockItem(glowlightCandle);
        }
    }

    private static DeferredBlock<Block> candle(String name, ResourceLocation flame, MapColor color) {
        return ModBlocks.BLOCKS.register(name, () -> new SpecialCandleBlock(flame, BlockBehaviour.Properties.of()
                .mapColor(color).noOcclusion().strength(0.1F).sound(SoundType.CANDLE)
                .lightLevel(SpecialCandleBlock.DIM_LIGHT_EMISSION).pushReaction(PushReaction.DESTROY)));
    }

    private static DeferredBlock<Block> cake(String name, DeferredBlock<Block> candle, ResourceLocation flame) {
        return ModBlocks.BLOCKS.register(name, () -> new SpecialCandleCakeBlock(candle.get(), flame, BlockBehaviour.Properties.of()
                .forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)
                .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 2 : 0)));
    }
}
