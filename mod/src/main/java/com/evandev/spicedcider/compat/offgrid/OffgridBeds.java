package com.evandev.spicedcider.compat.offgrid;

import dev.tazer.offgrid.world.Offsets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OffgridBeds {
    public static boolean placed(ServerLevel level, BlockPos pos, BlockState state) {
        if (!state.hasProperty(BlockStateProperties.BED_PART) || !state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return true;
        Vec3 offset = Offsets.at(level, pos);
        if (offset == null) return true;
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos other = state.getValue(BlockStateProperties.BED_PART) == BedPart.FOOT ? pos.relative(facing) : pos.relative(facing.getOpposite());
        if (!level.getBlockState(other).is(state.getBlock()) || Offsets.at(level, other) != null) return true;
        if (Math.abs(supportTop(level, pos) - supportTop(level, other)) > 1.0E-4) return false;
        Offsets.set(level, other, offset);
        return true;
    }

    private static double supportTop(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        VoxelShape shape = level.getBlockState(below).getCollisionShape(level, below);
        return shape.isEmpty() ? 0.0 : shape.max(Direction.Axis.Y);
    }
}
