package com.evandev.spicedcider.lodgen;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class LodLight {
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int OPACITY = 0xF;
    private static final int EMISSION_SHIFT = 4;
    private static final int SHAPED = 1 << 8;

    private final ServerLevel level;
    private final boolean hasSky;
    private final Reference2IntOpenHashMap<BlockState> properties = new Reference2IntOpenHashMap<>();
    private final IntArrayFIFOQueue queue = new IntArrayFIFOQueue();
    private byte[] block = new byte[0];
    private byte[] sky = new byte[0];
    private int[] bottom = new int[0];

    private ChunkAccess[] chunks;
    private int sizeX;
    private int sizeZ;
    private int width;
    private int depth;
    private int height;

    LodLight(ServerLevel level) {
        this.level = level;
        this.hasSky = level.dimensionType().hasSkyLight();
        this.properties.defaultReturnValue(-1);
    }

    void solve(ChunkAccess[] chunks, int sizeX, int sizeZ) {
        this.chunks = chunks;
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        this.width = sizeX * 16;
        this.depth = sizeZ * 16;

        int top = -1;
        for (ChunkAccess chunk : chunks) {
            if (chunk != null) top = Math.max(top, chunk.getHighestFilledSectionIndex());
        }
        this.height = (Math.min(top + 1, level.getSectionsCount() - 1) + 1) * 16;

        int volume = width * depth * height;
        if (block.length < volume) {
            block = new byte[volume];
            sky = new byte[volume];
        } else {
            Arrays.fill(block, 0, volume, (byte) 0);
            Arrays.fill(sky, 0, volume, (byte) 0);
        }
        if (bottom.length < width * depth) bottom = new int[width * depth];

        seedBlockLight();
        propagate(block);
        if (hasSky) {
            seedSkyLight();
            propagate(sky);
        }
    }

    List<LodColumn.Section> sections(ChunkAccess chunk, int gridX, int gridZ) {
        int top = chunk.getHighestFilledSectionIndex();
        if (top < 0) return List.of();
        int last = Math.min(top + 1, chunk.getSectionsCount() - 1);
        List<LodColumn.Section> sections = new ArrayList<>(last + 1);
        for (int i = 0; i <= last; i++) {
            LevelChunkSection section = chunk.getSection(i);
            DataLayer blockLight = layer(block, gridX, gridZ, i);
            DataLayer skyLight = hasSky ? layer(sky, gridX, gridZ, i) : null;
            sections.add(new LodColumn.Section(level.getSectionYFromSectionIndex(i), section, blockLight, skyLight));
        }
        return sections;
    }

    private DataLayer layer(byte[] light, int gridX, int gridZ, int sectionIndex) {
        if (sectionIndex * 16 >= height) return light == sky ? new DataLayer(15) : null;
        byte[] data = null;
        int baseX = gridX * 16;
        int baseZ = gridZ * 16;
        for (int y = 0; y < 16; y++) {
            int row = (sectionIndex * 16 + y) * depth;
            for (int z = 0; z < 16; z++) {
                int start = (row + baseZ + z) * width + baseX;
                for (int x = 0; x < 16; x++) {
                    int value = light[start + x];
                    if (value == 0) continue;
                    if (data == null) data = new byte[DataLayer.SIZE];
                    int index = y << 8 | z << 4 | x;
                    data[index >> 1] |= (byte) (value << ((index & 1) << 2));
                }
            }
        }
        return data == null ? null : new DataLayer(data);
    }

    private void seedBlockLight() {
        for (int gz = 0; gz < sizeZ; gz++) {
            for (int gx = 0; gx < sizeX; gx++) {
                ChunkAccess chunk = chunks[gz * sizeX + gx];
                if (chunk == null) continue;
                for (int i = 0; i * 16 < height; i++) {
                    LevelChunkSection section = chunk.getSection(i);
                    if (section.hasOnlyAir() || !section.getStates().maybeHas(state -> emission(state) > 0)) continue;
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            for (int x = 0; x < 16; x++) {
                                int emission = emission(section.getBlockState(x, y, z));
                                if (emission == 0) continue;
                                int index = index(gx * 16 + x, i * 16 + y, gz * 16 + z);
                                block[index] = (byte) emission;
                                queue.enqueue(index);
                            }
                        }
                    }
                }
            }
        }
    }

    private void seedSkyLight() {
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                int column = z * width + x;
                if (chunks[(z >> 4) * sizeX + (x >> 4)] == null) {
                    bottom[column] = height;
                    continue;
                }
                BlockState above = AIR;
                int y = height - 1;
                for (; y >= 0; y--) {
                    BlockState state = state(x, y, z);
                    if (stopsSky(above, state)) break;
                    sky[index(x, y, z)] = 15;
                    above = state;
                }
                bottom[column] = y + 1;
            }
        }
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                int low = bottom[z * width + x];
                if (low >= height) continue;
                int reach = low;
                if (x > 0) reach = Math.max(reach, bottom[z * width + x - 1]);
                if (x < width - 1) reach = Math.max(reach, bottom[z * width + x + 1]);
                if (z > 0) reach = Math.max(reach, bottom[(z - 1) * width + x]);
                if (z < depth - 1) reach = Math.max(reach, bottom[(z + 1) * width + x]);
                reach = Math.min(reach, height - 1);
                for (int y = low; y <= reach; y++) {
                    queue.enqueue(index(x, y, z));
                }
            }
        }
    }

    private void propagate(byte[] light) {
        int layer = width * depth;
        while (!queue.isEmpty()) {
            int index = queue.dequeueInt();
            int value = light[index];
            if (value <= 1) continue;
            int x = index % width;
            int z = index / width % depth;
            int y = index / layer;
            BlockState source = null;
            for (Direction direction : DIRECTIONS) {
                int nx = x + direction.getStepX();
                int ny = y + direction.getStepY();
                int nz = z + direction.getStepZ();
                if (nx < 0 || nx >= width || nz < 0 || nz >= depth || ny < 0 || ny >= height) continue;
                int neighbor = index(nx, ny, nz);
                if (light[neighbor] >= value - 1) continue;
                BlockState target = state(nx, ny, nz);
                if (target == null) continue;
                int targetProperties = properties(target);
                int next = value - Math.max(1, targetProperties & OPACITY);
                if (next <= light[neighbor]) continue;
                if (source == null) source = state(x, y, z);
                if (((targetProperties | properties(source)) & SHAPED) != 0 && occludes(source, target, direction)) continue;
                light[neighbor] = (byte) next;
                queue.enqueue(neighbor);
            }
        }
    }

    private boolean stopsSky(BlockState above, BlockState state) {
        if (state == null) return true;
        int stateProperties = properties(state);
        if ((stateProperties & OPACITY) != 0) return true;
        return ((stateProperties | properties(above)) & SHAPED) != 0 && occludes(above, state, Direction.DOWN);
    }

    private boolean occludes(BlockState source, BlockState target, Direction direction) {
        return Shapes.faceShapeOccludes(faceShape(source, direction), faceShape(target, direction.getOpposite()));
    }

    private VoxelShape faceShape(BlockState state, Direction direction) {
        if ((properties(state) & SHAPED) == 0) return Shapes.empty();
        return state.getFaceOcclusionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, direction);
    }

    private int emission(BlockState state) {
        return properties(state) >> EMISSION_SHIFT & OPACITY;
    }

    private int properties(BlockState state) {
        int cached = properties.getInt(state);
        if (cached != -1) return cached;
        int opacity = Math.min(15, state.getLightBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
        int emission = Math.min(15, state.getLightEmission(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
        int shaped = state.canOcclude() && state.useShapeForLightOcclusion() ? SHAPED : 0;
        int value = opacity | emission << EMISSION_SHIFT | shaped;
        properties.put(state, value);
        return value;
    }

    private BlockState state(int x, int y, int z) {
        ChunkAccess chunk = chunks[(z >> 4) * sizeX + (x >> 4)];
        if (chunk == null) return null;
        LevelChunkSection section = chunk.getSection(y >> 4);
        return section.hasOnlyAir() ? AIR : section.getBlockState(x & 15, y & 15, z & 15);
    }

    private int index(int x, int y, int z) {
        return (y * depth + z) * width + x;
    }
}
