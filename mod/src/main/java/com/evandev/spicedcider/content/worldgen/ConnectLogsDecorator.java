package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.content.block.FruitLogBlock;
import com.evandev.spicedcider.registry.ModTreeFeatures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.HashSet;
import java.util.Set;

public class ConnectLogsDecorator extends TreeDecorator {
    public static final ConnectLogsDecorator INSTANCE = new ConnectLogsDecorator();
    public static final MapCodec<ConnectLogsDecorator> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeFeatures.CONNECT_LOGS.get();
    }

    @Override
    public void place(Context context) {
        Set<BlockPos> logs = new HashSet<>(context.logs());
        Set<BlockPos> leaves = new HashSet<>(context.leaves());
        for (BlockPos pos : context.logs()) {
            BlockState[] found = new BlockState[1];
            context.level().isStateAtPosition(pos, state -> {
                found[0] = state;
                return true;
            });
            BlockState state = found[0];
            if (state == null || !(state.getBlock() instanceof FruitLogBlock)) continue;
            int leafCount = 0;
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = pos.relative(direction);
                state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), logs.contains(neighbor));
                if (leaves.contains(neighbor)) leafCount++;
            }
            context.setBlock(pos, state.setValue(FruitLogBlock.LEAFY, leafCount > 2));
        }
    }
}
