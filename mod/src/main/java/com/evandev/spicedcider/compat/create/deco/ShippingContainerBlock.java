package com.evandev.spicedcider.compat.create.deco;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.content.logistics.vault.ItemVaultBlock;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryWrapper;
import com.simibubi.create.foundation.utility.SameSizeCombinedInvWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ShippingContainerBlock extends ItemVaultBlock {
    private final DyeColor color;

    public ShippingContainerBlock(Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    public boolean isSameType(BlockState other) {
        return other.getBlock() instanceof ShippingContainerBlock container && container.color == color;
    }

    public static boolean isContainer(BlockState state) {
        return state.getBlock() instanceof ShippingContainerBlock;
    }

    public static boolean isLargeContainer(BlockState state) {
        return isContainer(state) && state.getValue(LARGE);
    }

    @Nullable
    public static Direction.Axis getContainerAxis(BlockState state) {
        return isContainer(state) ? state.getValue(HORIZONTAL_AXIS) : null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) {
            BlockState placedOn = context.getLevel().getBlockState(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
            if (isSameType(placedOn)) {
                return defaultBlockState().setValue(HORIZONTAL_AXIS, placedOn.getValue(HORIZONTAL_AXIS));
            }
        }
        return defaultBlockState().setValue(HORIZONTAL_AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    public BlockEntityType<? extends ItemVaultBlockEntity> getBlockEntityType() {
        return CreateDecoBlocks.CONTAINER_ENTITIES.get(color).get();
    }

    public static class Entity extends ItemVaultBlockEntity {
        public Entity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Nullable
        public IItemHandler getItemHandler() {
            initContainerCapability();
            return itemCapability == null ? null : itemCapability.getCapability();
        }

        @Override
        public InventoryIdentifier getInvId() {
            initContainerCapability();
            return invId;
        }

        @Override
        public Entity getControllerBE() {
            if (isController()) return this;
            if (controller == null || level == null) return null;
            BlockEntity blockEntity = level.getBlockEntity(controller);
            return blockEntity instanceof Entity entity ? entity : null;
        }

        @Override
        protected void updateConnectivity() {
            updateConnectivity = false;
            if (level.isClientSide() || !isController()) return;
            ConnectivityHandler.formMulti(this);
        }

        @Override
        public void notifyMultiUpdated() {
            BlockState state = getBlockState();
            if (isContainer(state)) {
                level.setBlock(getBlockPos(), state.setValue(LARGE, radius > 2), Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
            }
            super.notifyMultiUpdated();
        }

        @Override
        public void removeController(boolean keepContents) {
            BlockState state = getBlockState();
            if (isContainer(state) && level != null && !level.isClientSide()) {
                level.setBlock(worldPosition, state.setValue(LARGE, false), Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
            }
            super.removeController(keepContents);
        }

        private void initContainerCapability() {
            if (itemCapability != null && itemCapability.getCapability() != null) return;
            if (!isController()) {
                Entity controllerBE = getControllerBE();
                if (controllerBE == null) return;
                controllerBE.initContainerCapability();
                itemCapability = ICapabilityProvider.of(() -> {
                    if (controllerBE.isRemoved() || controllerBE.itemCapability == null) return null;
                    return controllerBE.itemCapability.getCapability();
                });
                invId = controllerBE.invId;
                return;
            }

            boolean alongZ = getContainerAxis(getBlockState()) == Direction.Axis.Z;
            IItemHandlerModifiable[] invs = new IItemHandlerModifiable[length * radius * radius];
            for (int yOffset = 0; yOffset < length; yOffset++) {
                for (int xOffset = 0; xOffset < radius; xOffset++) {
                    for (int zOffset = 0; zOffset < radius; zOffset++) {
                        BlockPos partPos = alongZ ? worldPosition.offset(xOffset, zOffset, yOffset) : worldPosition.offset(yOffset, xOffset, zOffset);
                        Entity part = ConnectivityHandler.partAt(getType(), getLevel(), partPos);
                        invs[yOffset * radius * radius + xOffset * radius + zOffset] = part != null ? part.inventory : new ItemStackHandler();
                    }
                }
            }
            itemCapability = ICapabilityProvider.of(new VersionedInventoryWrapper(SameSizeCombinedInvWrapper.create(invs)));
            BlockPos farCorner = alongZ ? worldPosition.offset(radius, radius, length) : worldPosition.offset(length, radius, radius);
            invId = new InventoryIdentifier.Bounds(BoundingBox.fromCorners(worldPosition, farCorner));
        }
    }
}
