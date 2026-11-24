package com.evandev.spicedcider.compat.create;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.content.vault.VaultMenu;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.IItemHandler;

public class VaultInterface {
    private static final double REACH = 4.0;
    private static final ResourceLocation WRENCH = ResourceLocation.fromNamespaceAndPath("create", "wrench");

    public static void register() {
        NeoForge.EVENT_BUS.addListener(VaultInterface::onRightClickBlock);
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.itemVaultInterface, false)) return;
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!(level.getBlockState(pos).getBlock() instanceof ItemVaultBlock)) return;
        if (player.isSecondaryUseActive() || blocksOpening(player.getMainHandItem())) return;
        if (!(level.getBlockEntity(pos) instanceof ItemVaultBlockEntity vault)) return;
        ItemVaultBlockEntity controller = vault.getControllerBE();
        if (controller == null) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
        if (player instanceof ServerPlayer serverPlayer) {
            open(serverPlayer, level, pos, controller);
        }
    }

    private static boolean blocksOpening(ItemStack stack) {
        return stack.getItem() instanceof BlockItem || stack.is(Tags.Items.TOOLS_WRENCH) || WRENCH.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    private static void open(ServerPlayer player, Level level, BlockPos pos, ItemVaultBlockEntity controller) {
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, controller.getBlockPos(), null);
        if (handler == null) return;
        BlockPos controllerPos = controller.getBlockPos();
        int width = controller.getWidth();
        int length = controller.getHeight();
        int slots = handler.getSlots();
        player.openMenu(new SimpleMenuProvider(
                (id, inventory, p) -> new VaultMenu(id, inventory, handler, viewer -> stillValid(viewer, level, pos, controllerPos, width, length, handler)),
                level.getBlockState(pos).getBlock().getName()), buf -> buf.writeVarInt(slots));
        level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.5F, 0.8F + level.random.nextFloat() * 0.1F);
    }

    private static boolean stillValid(Player player, Level level, BlockPos pos, BlockPos controllerPos, int width, int length, IItemHandler handler) {
        if (!player.canInteractWithBlock(pos, REACH)) return false;
        if (!(level.getBlockEntity(pos) instanceof ItemVaultBlockEntity vault) || vault.isRemoved()) return false;
        ItemVaultBlockEntity controller = vault.getControllerBE();
        if (controller == null || controller.isRemoved() || !controller.getBlockPos().equals(controllerPos)) return false;
        if (controller.getWidth() != width || controller.getHeight() != length) return false;
        return level.getCapability(Capabilities.ItemHandler.BLOCK, controllerPos, null) == handler;
    }
}
