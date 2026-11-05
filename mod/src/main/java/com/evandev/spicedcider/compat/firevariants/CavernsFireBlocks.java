package com.evandev.spicedcider.compat.firevariants;

import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.teamabnormals.caverns_and_chasms.common.block.BrazierBlock;
import com.teamabnormals.caverns_and_chasms.core.registry.CCBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class CavernsFireBlocks {
    public static DeferredBlock<Block> sulfurBrazier;
    public static DeferredItem<BlockItem> sulfurBrazierItem;

    public static void register() {
        sulfurBrazier = ModBlocks.BLOCKS.register("sulfur_brazier", () -> new BrazierBlock(1.0F, CCBlocks.CCProperties.BRAZIER_DIM));
        sulfurBrazierItem = ModItems.ITEMS.registerSimpleBlockItem(sulfurBrazier);
    }
}
