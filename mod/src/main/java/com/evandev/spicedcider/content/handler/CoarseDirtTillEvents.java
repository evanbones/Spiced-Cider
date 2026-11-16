package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CoarseDirtTillEvents {

    @SubscribeEvent
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() == ItemAbilities.HOE_TILL
                && event.getFinalState() == event.getState()
                && event.getState().is(Blocks.COARSE_DIRT)) {
            event.setFinalState(Blocks.DIRT.defaultBlockState());
        }
    }
}
