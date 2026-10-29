package com.evandev.spicedcider.mixin.perf;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(WallBlock.class)
public abstract class WallBlockShapeCacheMixin extends Block {
    @Unique
    private static final Map<List<Float>, VoxelShape[]> spicedcider$SHAPES = new ConcurrentHashMap<>();

    protected WallBlockShapeCacheMixin(Properties properties) {
        super(properties);
    }

    @WrapMethod(method = "makeShapes")
    private Map<BlockState, VoxelShape> spicedcider$shareShapes(float width, float depth, float wallPostHeight, float wallMinY, float wallLowHeight, float wallTallHeight, Operation<Map<BlockState, VoxelShape>> original) {
        List<Float> key = List.of(width, depth, wallPostHeight, wallMinY, wallLowHeight, wallTallHeight);
        VoxelShape[] shapes = spicedcider$SHAPES.get(key);
        if (shapes == null) {
            Map<BlockState, VoxelShape> built = original.call(width, depth, wallPostHeight, wallMinY, wallLowHeight, wallTallHeight);
            shapes = new VoxelShape[2 * 3 * 3 * 3 * 3];
            for (Map.Entry<BlockState, VoxelShape> entry : built.entrySet()) {
                shapes[spicedcider$index(entry.getKey())] = entry.getValue();
            }
            for (VoxelShape shape : shapes) {
                if (shape == null) return built;
            }
            spicedcider$SHAPES.putIfAbsent(key, shapes);
            return built;
        }
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (Boolean up : WallBlock.UP.getPossibleValues()) {
            for (WallSide east : WallBlock.EAST_WALL.getPossibleValues()) {
                for (WallSide north : WallBlock.NORTH_WALL.getPossibleValues()) {
                    for (WallSide west : WallBlock.WEST_WALL.getPossibleValues()) {
                        for (WallSide south : WallBlock.SOUTH_WALL.getPossibleValues()) {
                            BlockState state = defaultBlockState()
                                    .setValue(WallBlock.UP, up)
                                    .setValue(WallBlock.EAST_WALL, east)
                                    .setValue(WallBlock.WEST_WALL, west)
                                    .setValue(WallBlock.NORTH_WALL, north)
                                    .setValue(WallBlock.SOUTH_WALL, south);
                            VoxelShape shape = shapes[spicedcider$index(state)];
                            builder.put(state.setValue(WallBlock.WATERLOGGED, false), shape);
                            builder.put(state.setValue(WallBlock.WATERLOGGED, true), shape);
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    @Unique
    private static int spicedcider$index(BlockState state) {
        int index = state.getValue(WallBlock.UP) ? 1 : 0;
        index = index * 3 + state.getValue(WallBlock.EAST_WALL).ordinal();
        index = index * 3 + state.getValue(WallBlock.NORTH_WALL).ordinal();
        index = index * 3 + state.getValue(WallBlock.WEST_WALL).ordinal();
        return index * 3 + state.getValue(WallBlock.SOUTH_WALL).ordinal();
    }
}
