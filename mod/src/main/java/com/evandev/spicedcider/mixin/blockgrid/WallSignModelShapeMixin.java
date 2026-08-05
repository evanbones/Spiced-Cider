package com.evandev.spicedcider.mixin.blockgrid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.EnumMap;
import java.util.Map;

@Mixin(WallSignBlock.class)
public class WallSignModelShapeMixin {
    @Unique
    private static final double spicedcider$THICKNESS = 2.0D;
    @Unique
    private static final double spicedcider$BOTTOM = 4.0D;
    @Unique
    private static final double spicedcider$TOP = 13.0D;
    @Unique
    private static final double spicedcider$BLOCK = 16.0D;

    @Unique
    private static final Map<Direction, VoxelShape> spicedcider$SHAPES = spicedcider$buildShapes();

    @Unique
    private static Map<Direction, VoxelShape> spicedcider$buildShapes() {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            shapes.put(facing, spicedcider$plateBehind(facing));
        }
        return shapes;
    }

    @Unique
    private static VoxelShape spicedcider$plateBehind(Direction facing) {
        Direction wall = facing.getOpposite();
        double minX = wall.getStepX() > 0 ? spicedcider$BLOCK - spicedcider$THICKNESS : 0.0D;
        double maxX = wall.getStepX() < 0 ? spicedcider$THICKNESS : spicedcider$BLOCK;
        double minZ = wall.getStepZ() > 0 ? spicedcider$BLOCK - spicedcider$THICKNESS : 0.0D;
        double maxZ = wall.getStepZ() < 0 ? spicedcider$THICKNESS : spicedcider$BLOCK;
        return Block.box(minX, spicedcider$BOTTOM, minZ, maxX, spicedcider$TOP, maxZ);
    }

    @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
    private VoxelShape spicedcider$modelAccurateWallShape(VoxelShape original, BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return original;
        }
        VoxelShape shape = spicedcider$SHAPES.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
        return shape == null ? original : shape;
    }
}
