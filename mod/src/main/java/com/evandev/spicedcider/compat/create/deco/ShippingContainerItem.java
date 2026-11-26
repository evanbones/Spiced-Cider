package com.evandev.spicedcider.compat.create.deco;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.logistics.vault.ItemVaultItem;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ShippingContainerItem extends ItemVaultItem {
    public ShippingContainerItem(ShippingContainerBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result.consumesAction()) {
            tryMultiPlace(context);
        }
        return result;
    }

    private void tryMultiPlace(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isShiftKeyDown()) return;
        ShippingContainerBlock block = (ShippingContainerBlock) getBlock();
        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos placedOn = pos.relative(face.getOpposite());
        BlockState placedOnState = level.getBlockState(placedOn);
        if (!block.isSameType(placedOnState)) return;

        ShippingContainerBlock.Entity part = ConnectivityHandler.partAt(block.getBlockEntityType(), level, placedOn);
        if (part == null) return;
        ShippingContainerBlock.Entity controller = part.getControllerBE();
        if (controller == null) return;
        int width = controller.getWidth();
        if (width == 1) return;

        Direction.Axis axis = ShippingContainerBlock.getContainerAxis(placedOnState);
        if (axis == null || axis != face.getAxis()) return;
        Direction facing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        BlockPos start = face == facing.getOpposite()
                ? controller.getBlockPos().relative(facing.getOpposite())
                : controller.getBlockPos().relative(facing, controller.getHeight());
        if (VecHelper.getCoordinate(start, axis) != VecHelper.getCoordinate(pos, axis)) return;

        int needed = 0;
        for (int a = 0; a < width; a++) {
            for (int b = 0; b < width; b++) {
                BlockPos offset = axis == Direction.Axis.X ? start.offset(0, a, b) : start.offset(a, b, 0);
                BlockState other = level.getBlockState(offset);
                if (ShippingContainerBlock.isContainer(other)) continue;
                if (!other.canBeReplaced()) return;
                needed++;
            }
        }
        if (!player.isCreative() && stack.getCount() < needed) return;
        for (int a = 0; a < width; a++) {
            for (int b = 0; b < width; b++) {
                BlockPos offset = axis == Direction.Axis.X ? start.offset(0, a, b) : start.offset(a, b, 0);
                if (block.isSameType(level.getBlockState(offset))) continue;
                super.place(BlockPlaceContext.at(context, offset, face));
            }
        }
    }
}
