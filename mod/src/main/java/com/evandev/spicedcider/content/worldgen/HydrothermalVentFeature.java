package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.content.block.TubewormBlock;
import com.evandev.spicedcider.content.block.TubewormPart;
import com.evandev.spicedcider.content.block.VentBlock;
import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class HydrothermalVentFeature extends Feature<NoneFeatureConfiguration> {
    public HydrothermalVentFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockPos floor = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, origin).below();
        if (!isWater(level, floor.above()) || !isWater(level, floor.above(6))) return false;

        int radius = 2 + random.nextInt(2);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz) + random.nextFloat() * 0.8F;
                if (distance > radius + 0.5) continue;
                int top = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, floor.getX() + dx, floor.getZ() + dz) - 1;
                int mound = distance < radius - 1 ? 1 : 0;
                for (int y = top - 2; y <= top + mound; y++) {
                    pos.set(floor.getX() + dx, y, floor.getZ() + dz);
                    BlockState state = level.getBlockState(pos);
                    if (y <= top ? isFloor(state) : isWater(level, pos)) {
                        level.setBlock(pos, ModBlocks.SULFIDE.get().defaultBlockState(), 2);
                    }
                }
            }
        }

        int chimneys = 1 + random.nextInt(3);
        for (int i = 0; i < chimneys; i++) {
            int x = floor.getX() + random.nextInt(radius * 2 - 1) - radius + 1;
            int z = floor.getZ() + random.nextInt(radius * 2 - 1) - radius + 1;
            BlockPos base = surface(level, x, floor.getY(), z);
            if (isWater(level, base.below())) continue;
            BlockState vent = (random.nextInt(4) == 0 ? ModBlocks.CALCITE_VENT : ModBlocks.SULFIDE_VENT).get().defaultBlockState()
                    .setValue(VentBlock.AXIS, Direction.Axis.Y).setValue(VentBlock.WATERLOGGED, true);
            int height = 1 + random.nextInt(4);
            for (int y = 0; y < height && isWater(level, base.above(y)) && isWater(level, base.above(y + 1)); y++) {
                level.setBlock(base.above(y), vent, 2);
            }
        }

        int worms = 3 + random.nextInt(6);
        for (int i = 0; i < worms; i++) {
            int x = floor.getX() + random.nextInt(radius * 2 + 3) - radius - 1;
            int z = floor.getZ() + random.nextInt(radius * 2 + 3) - radius - 1;
            BlockPos base = surface(level, x, floor.getY(), z);
            if (!level.getBlockState(base.below()).is(ModBlocks.SULFIDE.get())) continue;
            int height = 1 + random.nextInt(3);
            for (int y = 0; y < height; y++) {
                BlockPos at = base.above(y);
                if (!isWater(level, at) || !isWater(level, at.above())) break;
                level.setBlock(at, ModBlocks.TUBEWORM.get().defaultBlockState()
                        .setValue(TubewormBlock.PART, y == height - 1 ? TubewormPart.TOP : TubewormPart.MIDDLE), 2);
            }
        }
        return true;
    }

    private static BlockPos surface(WorldGenLevel level, int x, int floorY, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, floorY + 4, z);
        while (pos.getY() > floorY - 4 && isWater(level, pos.below())) {
            pos.move(Direction.DOWN);
        }
        return pos.immutable();
    }

    private static boolean isWater(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.WATER);
    }

    private static boolean isFloor(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.SAND) || state.is(BlockTags.DIRT)
                || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) || state.is(Blocks.MAGMA_BLOCK) || state.is(ModBlocks.ALGAL_GRAVEL.get())
                || state.is(ModBlocks.DRIED_DEEPSILT.get());
    }
}
