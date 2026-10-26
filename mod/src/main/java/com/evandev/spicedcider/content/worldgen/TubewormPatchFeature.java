package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.content.block.TubewormBlock;
import com.evandev.spicedcider.content.block.TubewormPart;
import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TubewormPatchFeature extends Feature<NoneFeatureConfiguration> {
    public TubewormPatchFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int placed = 0;
        int tries = 8 + random.nextInt(10);
        for (int i = 0; i < tries; i++) {
            int x = origin.getX() + random.nextInt(9) - 4;
            int z = origin.getZ() + random.nextInt(9) - 4;
            BlockPos base = new BlockPos(x, level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z), z);
            if (!level.getBlockState(base.below()).isFaceSturdy(level, base.below(), Direction.UP)) continue;
            int height = 1 + random.nextInt(3);
            for (int y = 0; y < height; y++) {
                BlockPos at = base.above(y);
                if (!level.getBlockState(at).is(Blocks.WATER) || !level.getBlockState(at.above()).is(Blocks.WATER)) break;
                level.setBlock(at, ModBlocks.TUBEWORM.get().defaultBlockState()
                        .setValue(TubewormBlock.PART, y == height - 1 ? TubewormPart.TOP : TubewormPart.MIDDLE), 2);
                placed++;
            }
        }
        return placed > 0;
    }
}
