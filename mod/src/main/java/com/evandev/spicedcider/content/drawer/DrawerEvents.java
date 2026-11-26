package com.evandev.spicedcider.content.drawer;

import com.evandev.spicedcider.registry.ModBlockEntities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class DrawerEvents {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ITEM_DRAWER.get(), (drawer, side) -> drawer.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ITEM_DRAWER_DISTRIBUTOR.get(), (distributor, side) -> distributor.getItemHandler());
    }

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (!player.isCreative()) return;
        Level level = event.getLevel();
        BlockState state = level.getBlockState(event.getPos());
        if (!(state.getBlock() instanceof ItemDrawerBlock)) return;
        BlockHitResult hit = ItemDrawerBlock.rayTrace(level, player);
        if (hit.getType() != HitResult.Type.BLOCK || !hit.getBlockPos().equals(event.getPos()) || !ItemDrawerBlock.isFront(state, hit.getDirection())) return;
        event.setCanceled(true);
        if (!level.isClientSide && event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
            ItemDrawerBlock.takeFromFront(level, event.getPos(), state, player);
        }
    }
}
