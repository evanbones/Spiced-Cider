package com.evandev.spicedcider.entities.strawgolem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class StrawGolemLids {
    private static final int CHEST_LID_EVENT = 1;
    private static final Map<Class<?>, Optional<Field>> COUNTERS = new ConcurrentHashMap<>();

    public static boolean open(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        BlockState state = level.getBlockState(pos);
        if (blockEntity instanceof ChestBlockEntity) {
            level.blockEvent(pos, state.getBlock(), CHEST_LID_EVENT, 1);
            return true;
        }
        if (blockEntity != null && state.hasProperty(BlockStateProperties.OPEN) && !state.getValue(BlockStateProperties.OPEN)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.OPEN, true), 3);
            return true;
        }
        return false;
    }

    public static void close(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return;
        }
        int openers = playerOpeners(blockEntity);
        BlockState state = level.getBlockState(pos);
        if (blockEntity instanceof ChestBlockEntity) {
            level.blockEvent(pos, state.getBlock(), CHEST_LID_EVENT, Math.max(openers, 0));
        } else if (openers <= 0 && state.hasProperty(BlockStateProperties.OPEN) && state.getValue(BlockStateProperties.OPEN)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.OPEN, false), 3);
        }
    }

    private static int playerOpeners(BlockEntity blockEntity) {
        Optional<Field> field = COUNTERS.computeIfAbsent(blockEntity.getClass(), StrawGolemLids::findCounter);
        if (field.isEmpty()) {
            return 0;
        }
        try {
            return field.get().get(blockEntity) instanceof ContainerOpenersCounter counter ? counter.getOpenerCount() : 0;
        } catch (IllegalAccessException e) {
            return 0;
        }
    }

    private static Optional<Field> findCounter(Class<?> type) {
        for (Class<?> current = type; current != null && current != BlockEntity.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (ContainerOpenersCounter.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    return Optional.of(field);
                }
            }
        }
        return Optional.empty();
    }
}
