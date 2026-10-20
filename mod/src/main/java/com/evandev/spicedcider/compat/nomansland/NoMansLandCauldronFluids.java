package com.evandev.spicedcider.compat.nomansland;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModFluids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.RegisterCauldronFluidContentEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class NoMansLandCauldronFluids {
    private static final String NO_MANS_LAND = "nomansland";

    @SubscribeEvent
    public static void registerCauldronContent(RegisterCauldronFluidContentEvent event) {
        if (!ModList.get().isLoaded(NO_MANS_LAND)) return;
        register(event, "resin_cauldron", ModFluids.RESIN.get());
        register(event, "maple_syrup_cauldron", ModFluids.MAPLE_SYRUP.get());
    }

    private static void register(RegisterCauldronFluidContentEvent event, String path, Fluid fluid) {
        Block block = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath(NO_MANS_LAND, path)).orElse(null);
        if (block == null || CauldronFluidContent.getForBlock(block) != null) return;
        if (!(block.getStateDefinition().getProperty("level") instanceof IntegerProperty level)) return;
        event.register(block, fluid, FluidType.BUCKET_VOLUME, level);
    }
}
