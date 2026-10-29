package com.evandev.spicedcider.compat.toilandtrouble;

import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ToilAndTroubleCapabilities {

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY,
                (cauldron, side) -> new CauldronItemHandler(cauldron));
        event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, side) -> new WaterCauldronItemHandler(level, pos),
                Blocks.WATER_CAULDRON);
        if (ModList.get().isLoaded("create")) {
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY,
                    (cauldron, side) -> new CauldronFluidHandler(cauldron));
        }
    }
}
