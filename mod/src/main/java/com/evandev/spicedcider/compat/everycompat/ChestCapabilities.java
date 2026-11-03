package com.evandev.spicedcider.compat.everycompat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;

public class ChestCapabilities {

    public static void register(RegisterCapabilitiesEvent event) {
        List<Block> missing = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof ChestBlock && !event.isBlockRegistered(Capabilities.ItemHandler.BLOCK, block)) {
                missing.add(block);
            }
        }
        if (missing.isEmpty()) return;
        event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, side) -> {
            Container container = ChestBlock.getContainer((ChestBlock) state.getBlock(), state, level, pos, true);
            return container == null ? null : new InvWrapper(container);
        }, missing.toArray(Block[]::new));
    }
}
