package com.evandev.spicedcider.compat.create.deco.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.create.deco.ShippingContainerBlock;
import com.evandev.spicedcider.registry.ModDecoBlocks;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTModel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CreateDecoClient {
    private static final Map<ResourceLocation, ConnectedTextureBehaviour> BEHAVIOURS = new HashMap<>();

    public static void register(IEventBus modBus) {
        for (String metal : ModDecoBlocks.CATWALK_METALS) {
            String path = "block/catwalk/" + metal + "_catwalk";
            CTSpriteShiftEntry shift = CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL, id(path), id(path + "_connected"));
            BEHAVIOURS.put(id(metal + "_catwalk"), new CatwalkCT(shift));
        }
        for (DyeColor color : ModDecoBlocks.CONTAINER_COLORS) {
            BEHAVIOURS.put(id(color.getName() + "_shipping_container"), new ContainerCT(color));
        }
        modBus.addListener(CreateDecoClient::wrapModels);
    }

    private static void wrapModels(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        BEHAVIOURS.forEach((id, behaviour) -> {
            Block block = BuiltInRegistries.BLOCK.get(id);
            if (block == Blocks.AIR) return;
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
                BakedModel model = models.get(location);
                if (model != null && !(model instanceof CTModel)) {
                    models.put(location, new CTModel(model, behaviour));
                }
            }
        });
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, path);
    }

    private static class CatwalkCT extends SimpleCTBehaviour {
        CatwalkCT(CTSpriteShiftEntry shift) {
            super(shift);
        }

        @Override
        public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
            return face.getAxis().isVertical() && state.getBlock() == other.getBlock();
        }
    }

    private static class ContainerCT extends ConnectedTextureBehaviour.Base {
        private final Map<String, Couple<CTSpriteShiftEntry>> shifts = new HashMap<>();

        ContainerCT(DyeColor color) {
            for (String face : new String[]{"top", "bottom", "front", "side"}) {
                String prefix = "block/shipping_container/" + color.getName() + "/" + face;
                shifts.put(face, Couple.createWithContext(medium -> CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,
                        id(prefix + "_small"), id(prefix + (medium ? "_medium" : "_large")))));
            }
        }

        @Override
        public @Nullable CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
            Direction.Axis axis = ShippingContainerBlock.getContainerAxis(state);
            if (axis == null) return null;
            boolean small = !ShippingContainerBlock.isLargeContainer(state);
            String face = direction.getAxis() == axis ? "front" : direction == Direction.UP ? "top" : direction == Direction.DOWN ? "bottom" : "side";
            return shifts.get(face).get(small);
        }

        @Override
        protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
            Direction.Axis axis = ShippingContainerBlock.getContainerAxis(state);
            boolean alongX = axis == Direction.Axis.X;
            if (face.getAxis().isVertical() && alongX) return super.getUpDirection(reader, pos, state, face).getClockWise();
            if (face.getAxis() == axis || face.getAxis().isVertical()) return super.getUpDirection(reader, pos, state, face);
            return Direction.fromAxisAndDirection(axis, alongX ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
        }

        @Override
        protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
            Direction.Axis axis = ShippingContainerBlock.getContainerAxis(state);
            if (face.getAxis().isVertical() && axis == Direction.Axis.X) return super.getRightDirection(reader, pos, state, face).getClockWise();
            if (face.getAxis() == axis || face.getAxis().isVertical()) return super.getRightDirection(reader, pos, state, face);
            return Direction.fromAxisAndDirection(Direction.Axis.Y, face.getAxisDirection());
        }

        @Override
        public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
            return state == other && ConnectivityHandler.isConnected(reader, pos, otherPos);
        }
    }
}
