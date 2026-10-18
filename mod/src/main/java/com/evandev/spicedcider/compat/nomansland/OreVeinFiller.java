package com.evandev.spicedcider.compat.nomansland;

import com.evandev.spicedcider.mixin.nomansland.accessor.StateTestingPredicateAccessor;
import com.farcr.nomansland.common.registry.NMLRegistries;
import com.farcr.nomansland.common.world.orevein.OreVeinSystem;
import com.farcr.nomansland.common.world.orevein.OreVeinType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

import java.util.List;

public final class OreVeinFiller {
    private static final ResourceLocation ORE_VEIN_RANDOM = NMLRegistries.ORE_VEIN_KEY.location();

    public static void fill(List<OreVeinSystem.OreVeinInstance> veins, WorldGenLevel level, ChunkAccess chunk, RandomState randomState) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();
        int chunkHeight = chunk.getHeight();
        int chunkMinY = chunk.getMinBuildHeight();
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;

        for (OreVeinSystem.OreVeinInstance vein : veins) {
            minY = Math.min(minY, vein.minY());
            maxY = Math.max(maxY, vein.maxY());
        }

        int maxHeight = Integer.MIN_VALUE;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                maxHeight = Math.max(chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z), maxHeight);
            }
        }

        maxY = Math.min(maxY, maxHeight);
        minY = Math.max(minY, chunkMinY + 1);
        if (minY > maxY) return;

        VeinNoiseField field = VeinNoiseField.acquire(chunkHeight);
        try {
            field.begin(minY - chunkMinY, maxY - chunkMinY, chunkMinX, chunkMinY, chunkMinZ,
                    randomState.getOrCreateNoise(Noises.ORE_VEIN_A),
                    randomState.getOrCreateNoise(Noises.ORE_VEIN_B),
                    randomState.getOrCreateNoise(Noises.ORE_GAP));
            RandomSource fillRandom = randomState.getOrCreateRandomFactory(ORE_VEIN_RANDOM).at(chunkMinX, 0, chunkMinZ);

            int count = veins.size();
            OreVeinSystem.OreVeinInstance[] instances = veins.toArray(new OreVeinSystem.OreVeinInstance[0]);
            StateTestingPredicateAccessor[] stateTests = new StateTestingPredicateAccessor[count];
            for (int i = 0; i < count; i++) {
                stateTests[i] = stateTestFor(instances[i].type().targetCondition());
            }

            Context context = new Context(level, chunk, chunkMinY, field, fillRandom);
            for (int x = 0; x < 16; x++) {
                int worldX = x + chunkMinX;
                context.pos.setX(worldX);
                for (int z = 0; z < 16; z++) {
                    int worldZ = z + chunkMinZ;
                    context.pos.setZ(worldZ);
                    field.beginColumn();
                    for (int i = 0; i < count; i++) {
                        fillColumnForVein(instances[i], stateTests[i], x, z, worldX, worldZ, context);
                    }
                }
            }
        } finally {
            field.release();
        }
    }

    private static StateTestingPredicateAccessor stateTestFor(BlockPredicate predicate) {
        if (predicate instanceof StateTestingPredicate stateTesting) {
            StateTestingPredicateAccessor accessor = (StateTestingPredicateAccessor) stateTesting;
            if (Vec3i.ZERO.equals(accessor.spicedcider$getOffset())) return accessor;
        }
        return null;
    }

    private static void fillColumnForVein(OreVeinSystem.OreVeinInstance vein, StateTestingPredicateAccessor stateTest, int localX, int localZ, int worldX, int worldZ, Context context) {
        int minY = vein.minY();
        int maxY = vein.maxY();
        double xzDistSq = Mth.lengthSquared((double) (worldX - vein.x()), (double) (worldZ - vein.z()));
        if (xzDistSq > (double) vein.radiusSquared()) return;

        double xzDist = Math.sqrt(xzDistSq);
        ChunkAccess chunk = context.chunk;
        int chunkMinY = context.chunkMinY;
        int minColumnY = Math.max(minY, chunkMinY + 1);
        int maxColumnY = Math.min(chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, localX, localZ), maxY);
        LevelChunkSection section = null;
        int lastSectionIndex = Integer.MIN_VALUE;
        OreVeinType type = vein.type();
        BlockPos.MutableBlockPos pos = context.pos;
        RandomSource fillRandom = context.fillRandom;
        VeinNoiseField field = context.field;

        for (int y = maxColumnY; y >= minColumnY; y--) {
            int localY = y - chunkMinY;
            int sectionY = y & 15;
            int sectionIndex = chunk.getSectionIndex(y);
            if (sectionIndex != lastSectionIndex) {
                section = chunk.getSection(sectionIndex);
                lastSectionIndex = sectionIndex;
            }

            pos.setY(y);
            BlockState currentState = section.getBlockState(localX, sectionY, localZ);
            if (currentState.isAir()) continue;

            boolean target = stateTest != null && context.readsOwnChunk()
                    ? stateTest.spicedcider$testState(currentState)
                    : type.targetCondition().test(context.level, pos);
            if (!target) continue;

            int yDist = Math.min(maxY - y, y - minY);
            int yDiff = maxY - minY;
            if (fillRandom.nextFloat() > type.filler().probability()) continue;

            field.sample(localX, localY, localZ);
            double veinRidgeNoise = Math.max(Math.abs(field.veinA(localY)), Math.abs(field.veinB(localY)));
            if (type.invert()) {
                veinRidgeNoise = 1.0 - veinRidgeNoise;
            }

            float veinRadius = vein.veinRadius();
            veinRadius = Mth.clampedMap((float) yDist, 0.0F, (float) yDiff * 0.25F, 0.0F, veinRadius);
            veinRadius = (float) Mth.clampedMap(xzDist, (double) ((float) vein.radius() * 0.75F), (double) vein.radius(), (double) veinRadius, 0.0);
            veinRidgeNoise = veinRidgeNoise * 64.0 - (double) veinRadius;
            if (veinRidgeNoise >= fillRandom.triangle(0.0, (double) type.filler().incoherence())) continue;

            if (field.gap(localY) > -0.3F
                    && fillRandom.nextFloat() <= type.core().probability()
                    && -veinRidgeNoise >= fillRandom.triangle((double) veinRadius * 0.5, (double) (type.core().incoherence() * 0.5F))) {
                section.setBlockState(localX, sectionY, localZ, type.core().resolve(fillRandom, pos, currentState), false);
            } else {
                section.setBlockState(localX, sectionY, localZ, type.filler().resolve(fillRandom, pos, currentState), false);
            }
        }
    }

    private static final class Context {
        final WorldGenLevel level;
        final ChunkAccess chunk;
        final int chunkMinY;
        final VeinNoiseField field;
        final RandomSource fillRandom;
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        private int ownChunk;

        Context(WorldGenLevel level, ChunkAccess chunk, int chunkMinY, VeinNoiseField field, RandomSource fillRandom) {
            this.level = level;
            this.chunk = chunk;
            this.chunkMinY = chunkMinY;
            this.field = field;
            this.fillRandom = fillRandom;
        }

        boolean readsOwnChunk() {
            if (ownChunk == 0) {
                ChunkPos chunkPos = chunk.getPos();
                ownChunk = level instanceof WorldGenRegion region && region.getChunk(chunkPos.x, chunkPos.z) == chunk ? 1 : -1;
            }
            return ownChunk > 0;
        }
    }
}
