package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PrismarineSpikeFeature extends Feature<NoneFeatureConfiguration> {
    private static final float BUDDING_TOP_CHANCE = 0.8F;

    public PrismarineSpikeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockPos base = new BlockPos(origin.getX(), level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ()), origin.getZ());
        if (!isWater(level, base) || !isNaturalFloor(level.getBlockState(base.below()))) return false;

        int height = 4 + random.nextInt(9);
        float baseRadius = 1.0F + random.nextFloat() * (height > 8 ? 1.6F : 0.9F);
        float leanX = (random.nextFloat() - 0.5F) * 0.3F;
        float leanZ = (random.nextFloat() - 0.5F) * 0.3F;
        BlockState prismarine = Blocks.PRISMARINE.defaultBlockState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos top = base;

        for (int y = -2; y < height; y++) {
            float progress = Math.max(0, y) / (float) height;
            float radius = baseRadius * (1.0F - progress) + 0.35F;
            int cx = base.getX() + Math.round(leanX * Math.max(0, y));
            int cz = base.getZ() + Math.round(leanZ * Math.max(0, y));
            int reach = (int) Math.ceil(radius);
            for (int dx = -reach; dx <= reach; dx++) {
                for (int dz = -reach; dz <= reach; dz++) {
                    if (dx * dx + dz * dz > radius * radius + random.nextFloat() * 0.6F) continue;
                    pos.set(cx + dx, base.getY() + y, cz + dz);
                    if (y < 0 ? !level.getBlockState(pos).isAir() : isWater(level, pos)) {
                        level.setBlock(pos, prismarine, 2);
                    }
                }
            }
            if (y >= 0) top = new BlockPos(cx, base.getY() + y, cz);
        }

        if (level.getBlockState(top).is(Blocks.PRISMARINE) && random.nextFloat() < BUDDING_TOP_CHANCE) {
            level.setBlock(top, ModBlocks.BUDDING_PRISMARINE.get().defaultBlockState(), 2);
            BlockPos above = top.above();
            if (isWater(level, above) && random.nextBoolean()) {
                Block bud = random.nextInt(3) == 0 ? ModBlocks.PRISMARINE_CLUSTER.get() : ModBlocks.LARGE_PRISMARINE_BUD.get();
                level.setBlock(above, bud.defaultBlockState().setValue(AmethystClusterBlock.FACING, Direction.UP)
                        .setValue(AmethystClusterBlock.WATERLOGGED, true), 2);
            }
        }
        return true;
    }

    private static boolean isNaturalFloor(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.SAND) || state.is(Blocks.GRAVEL) || state.is(Blocks.PRISMARINE)
                || state.is(ModBlocks.BUDDING_PRISMARINE.get()) || state.is(ModBlocks.ACHROMARINE.get()) || state.is(ModBlocks.CHROMARINE.get());
    }

    private static boolean isWater(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.WATER);
    }
}
