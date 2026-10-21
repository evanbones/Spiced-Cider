package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.item.BalloonItem;
import com.evandev.spicedcider.entities.balloon.BalloonEntity;
import com.evandev.spicedcider.entities.balloon.BalloonTethers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class BalloonEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof BalloonItem balloon)) return;
        Entity target = event.getTarget();
        if (target instanceof BalloonEntity || !BalloonEntity.canHold(target)) return;
        if (!event.getLevel().isClientSide) {
            BalloonEntity.tieTo(target, balloon.getColor());
            stack.consume(1, event.getEntity());
        }
        event.getEntity().swing(event.getHand());
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        BalloonTethers.stow(event.getEntity());
    }

    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        for (DyeColor color : BalloonTethers.takeStowed(player)) {
            BalloonEntity.tieTo(player, color);
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        BalloonTethers.releaseAll(event.getEntity());
    }
}
