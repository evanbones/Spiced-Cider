package com.evandev.spicedcider.compat.offgrid;

import dev.tazer.offgrid.world.Offsets;
import com.evandev.spicedcider.SpicedCider;
import dev.tazer.offgrid.world.Overlays;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OffgridBeds {
    public static final TagKey<Block> SINKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "offgrid_sinks_onto_surfaces"));
    private static final Map<ServerLevel, Set<BlockPos>> PENDING_CLEARS = new WeakHashMap<>();

    public static void clearLater(ServerLevel level, BlockPos pos) {
        PENDING_CLEARS.computeIfAbsent(level, key -> new HashSet<>()).add(pos.immutable());
    }

    public static void flushClears() {
        if (PENDING_CLEARS.isEmpty()) return;
        for (Map.Entry<ServerLevel, Set<BlockPos>> entry : PENDING_CLEARS.entrySet()) {
            ServerLevel level = entry.getKey();
            for (BlockPos pos : entry.getValue()) {
                if (Offsets.at(level, pos) != null && Overlays.isRemoval(level.getBlockState(pos))) {
                    Offsets.set(level, pos, null);
                }
            }
        }
        PENDING_CLEARS.clear();
    }

    public static boolean placed(ServerLevel level, BlockPos pos, BlockState state) {
        if (!state.hasProperty(BlockStateProperties.BED_PART) || !state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return true;
        Vec3 offset = Offsets.at(level, pos);
        if (offset == null) return true;
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos other = state.getValue(BlockStateProperties.BED_PART) == BedPart.FOOT ? pos.relative(facing) : pos.relative(facing.getOpposite());
        if (!level.getBlockState(other).is(state.getBlock()) || Offsets.at(level, other) != null) return true;
        if (supportTop(level, other) > supportTop(level, pos) + 1.0E-4) return false;
        Offsets.set(level, other, offset);
        return true;
    }

    public static boolean otherHalfSinks(Level level, BlockPos pos, BlockState state) {
        if (!state.hasProperty(BlockStateProperties.BED_PART) || !state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return true;
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos other = state.getValue(BlockStateProperties.BED_PART) == BedPart.FOOT ? pos.relative(facing) : pos.relative(facing.getOpposite());
        return supportTop(level, other) <= supportTop(level, pos) + 1.0E-4;
    }

    private static double supportTop(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        VoxelShape shape = level.getBlockState(below).getCollisionShape(level, below);
        return shape.isEmpty() ? Double.NEGATIVE_INFINITY : shape.max(Direction.Axis.Y);
    }
}
