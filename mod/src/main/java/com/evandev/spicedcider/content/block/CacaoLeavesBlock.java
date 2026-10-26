package com.evandev.spicedcider.content.block;

import com.evandev.spicedcider.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CacaoLeavesBlock extends LeavesBlock implements BonemealableBlock {
    public static final MapCodec<CacaoLeavesBlock> CODEC = simpleCodec(CacaoLeavesBlock::new);
    private static final int POD_SPROUT_CHANCE = 40;

    public CacaoLeavesBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(PERSISTENT);
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (level.getBlockState(pos).is(this) && random.nextInt(POD_SPROUT_CHANCE) == 0 && canSprout(level, pos)) {
            sprout(level, pos);
        }
    }

    private static boolean canSprout(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isAir();
    }

    private static void sprout(Level level, BlockPos pos) {
        level.setBlock(pos.below(), ModBlocks.CACAO_POD.get().defaultBlockState(), 3);
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return canSprout(level, pos);
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        sprout(level, pos);
    }
}
