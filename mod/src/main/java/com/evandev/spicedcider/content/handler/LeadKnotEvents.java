package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.connectiblechains.ConnectibleChainsLeads;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class LeadKnotEvents {
    private static final boolean CONNECTIBLE_CHAINS = ModList.get().isLoaded("connectiblechains");

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!CONNECTIBLE_CHAINS) return;
        Entity target = event.getTarget();
        Player player = event.getEntity();
        Level level = event.getLevel();
        if (ConnectibleChainsLeads.isLeadKnot(target)) {
            BlockPos pos = target.blockPosition();
            if (LeadItem.leashableInArea(level, pos, leashable -> leashable.getLeashHolder() == player).isEmpty()) return;
            if (!level.isClientSide()) LeadItem.bindPlayerMobs(player, level, pos);
            succeed(event, level);
        } else if (target instanceof Leashable leashable && leashable.canBeLeashed() && !leashable.isLeashed()) {
            BlockPos pos = ConnectibleChainsLeads.heldLeadKnot(player, !level.isClientSide());
            if (pos == null) return;
            if (!level.isClientSide()) {
                LeashFenceKnotEntity knot = LeashFenceKnotEntity.getOrCreateKnot(level, pos);
                knot.playPlacementSound();
                leashable.setLeashedTo(knot, true);
            }
            succeed(event, level);
        }
    }

    private static void succeed(PlayerInteractEvent.EntityInteract event, Level level) {
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
        event.setCanceled(true);
    }
}
