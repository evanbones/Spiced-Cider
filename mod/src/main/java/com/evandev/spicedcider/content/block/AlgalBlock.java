package com.evandev.spicedcider.content.block;

import com.evandev.spicedcider.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AlgalBlock extends FallingBlock implements BonemealableBlock {

    public static final MapCodec<AlgalBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("baseBlock").forGetter(b -> b.baseBlock)
    ).apply(instance, AlgalBlock::new));

    private final Block baseBlock;

    public AlgalBlock(Properties properties, Block baseBlock) {
        super(properties);
        this.baseBlock = baseBlock;
    }

    @Override
    protected @NotNull MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.isClientSide && level.isWaterAt(pos.above())) {
            boolean hasKelp = false;
            for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-3, -2, -3), pos.offset(3, 2, 3))) {
                BlockState checkState = level.getBlockState(checkPos);
                if (checkState.is(Blocks.KELP) || checkState.is(Blocks.KELP_PLANT)) {
                    hasKelp = true;
                    break;
                }
            }

            if (hasKelp && random.nextInt(4) == 0) {
                BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);
                BlockState targetState = level.getBlockState(targetPos);
                BlockState newState = getAlgalVariant(targetState);

                if (newState != null && level.isWaterAt(targetPos.above())) {
                    level.setBlockAndUpdate(targetPos, newState);
                }
            }
        }
    }

    private BlockState getAlgalVariant(BlockState state) {
        if (state.is(Blocks.SAND)) return ModBlocks.ALGAL_SAND.get().defaultBlockState();
        if (state.is(Blocks.GRAVEL)) return ModBlocks.ALGAL_GRAVEL.get().defaultBlockState();
        return null;
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return level.isWaterAt(pos.above());
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        for (int i = 0; i < 4; i++) {
            BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);
            BlockState targetState = level.getBlockState(targetPos);
            BlockState newState = getAlgalVariant(targetState);
            if (newState != null && level.isWaterAt(targetPos.above())) {
                level.setBlockAndUpdate(targetPos, newState);
            }
        }
    }
}