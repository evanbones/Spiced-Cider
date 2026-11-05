package com.evandev.spicedcider.compat.firevariants;

import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModFireBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.teamabnormals.autumnity.common.block.LargeJackOLanternSliceBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class AutumnityFireBlocks {
    public static DeferredBlock<Block> largeSulfurSlice;
    public static DeferredItem<BlockItem> largeSulfurSliceItem;

    public static void register() {
        largeSulfurSlice = ModBlocks.BLOCKS.register("large_sulfur_jack_o_lantern_slice", () -> new LargeJackOLanternSliceBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.PUMPKIN).lightLevel(state -> ModFireBlocks.SULFUR_LIGHT)));
        largeSulfurSliceItem = ModItems.ITEMS.registerSimpleBlockItem(largeSulfurSlice);
    }
}
