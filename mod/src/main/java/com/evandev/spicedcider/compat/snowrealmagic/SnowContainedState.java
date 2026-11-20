package com.evandev.spicedcider.compat.snowrealmagic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Optional;

public final class SnowContainedState {
    private static final ClassValue<Optional<Method>> GETTER = new ClassValue<>() {
        @Override
        protected Optional<Method> computeValue(Class<?> type) {
            if (!type.getName().startsWith("snownee.snow.")) return Optional.empty();
            try {
                return Optional.of(type.getMethod("getContainedState"));
            } catch (NoSuchMethodException exception) {
                return Optional.empty();
            }
        }
    };

    public static BlockState unwrap(BlockGetter level, BlockPos pos, BlockState state) {
        BlockState contained = contained(level.getBlockEntity(pos));
        return contained == null || contained.isAir() ? state : contained;
    }

    @Nullable
    private static BlockState contained(@Nullable BlockEntity blockEntity) {
        if (blockEntity == null) return null;
        Optional<Method> getter = GETTER.get(blockEntity.getClass());
        if (getter.isEmpty()) return null;
        try {
            return getter.get().invoke(blockEntity) instanceof BlockState state ? state : null;
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }
}
