package com.evandev.spicedcider.content.block.fire;

import com.evandev.spicedcider.registry.ModTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SulfurFireBlock extends BaseFireBlock {
    public static final MapCodec<SulfurFireBlock> CODEC = simpleCodec(SulfurFireBlock::new);

    public SulfurFireBlock(Properties properties) {
        super(properties, 1.0F);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseFireBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        return this.canSurvive(state, level, currentPos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        return canSurviveOnBlock(level.getBlockState(pos.below()));
    }

    public static boolean canSurviveOnBlock(BlockState state) {
        return state.is(ModTags.Blocks.SULFUR_FIRE_BASE_BLOCKS);
    }

    @Override
    protected boolean canBurn(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        SulfurFumes.expose(entity);
        super.entityInside(state, level, pos, entity);
    }
}
