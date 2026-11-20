package com.evandev.spicedcider.content.casting;

import com.evandev.spicedcider.content.casting.smeltery.SmelteryBlock;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.evandev.spicedcider.registry.ModBlocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CastingCapabilities {
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CRUCIBLE.get(), (crucible, side) -> crucible.getItemHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CRUCIBLE.get(), (crucible, side) -> crucible.getFluidHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MOULD.get(), (mould, side) -> mould.getItemHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MOULD.get(), (mould, side) -> mould.getFluidHandler());
        event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, be, side) -> {
            var smeltery = SmelteryBlock.controller(level, pos, state);
            return smeltery == null ? null : smeltery.getItemHandler();
        }, ModBlocks.SMELTERY.get());
        event.registerBlock(Capabilities.FluidHandler.BLOCK, (level, pos, state, be, side) -> {
            var smeltery = SmelteryBlock.controller(level, pos, state);
            return smeltery == null ? null : smeltery.getFluidHandler();
        }, ModBlocks.SMELTERY.get());
    }
}
