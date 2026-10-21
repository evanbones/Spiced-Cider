package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.block.LatexCauldronBlock;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModFluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.RegisterCauldronFluidContentEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CauldronFluidEvents {

    @SubscribeEvent
    public static void registerCauldronContent(RegisterCauldronFluidContentEvent event) {
        event.register(ModBlocks.LATEX_CAULDRON.get(), ModFluids.LATEX.get(), FluidType.BUCKET_VOLUME, LatexCauldronBlock.LEVEL);
    }
}
