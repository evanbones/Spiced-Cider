package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.registry.ModTreeFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import org.jetbrains.annotations.Nullable;

public class MineshaftEntrance extends StructurePiece {
    private static final int MIN_SHAFT_HEIGHT = 6;
    private static final int TUNNEL_SEARCH_START = 3;
    private static final int TUNNEL_SEARCH_END = 22;
    private static final float HEADFRAME_CHANCE = 0.5F;

    private final MineshaftStructure.Type type;
    private final int centerX;
    private final int centerZ;
    private final int bottom;
    private final int roomTop;
    private final int top;
    private final Direction ladderWall;
    @Nullable
    private final Direction tunnel;
    private final int tunnelLength;

    private MineshaftEntrance(MineshaftStructure.Type type, int centerX, int centerZ, int bottom, int roomTop, int top,
                              Direction ladderWall, @Nullable Direction tunnel, int tunnelLength) {
        super(ModTreeFeatures.MINESHAFT_ENTRANCE.get(), 0, bounds(centerX, centerZ, bottom, top, tunnel, tunnelLength));
        this.type = type;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.bottom = bottom;
        this.roomTop = roomTop;
        this.top = top;
        this.ladderWall = ladderWall;
        this.tunnel = tunnel;
        this.tunnelLength = tunnelLength;
        setOrientation(null);
    }

    public MineshaftEntrance(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModTreeFeatures.MINESHAFT_ENTRANCE.get(), tag);
        this.type = MineshaftStructure.Type.byId(tag.getInt("MST"));
        this.centerX = tag.getInt("CX");
        this.centerZ = tag.getInt("CZ");
        this.bottom = tag.getInt("Bottom");
        this.roomTop = tag.getInt("RoomTop");
        this.top = tag.getInt("Top");
        this.ladderWall = Direction.from2DDataValue(tag.getInt("Ladder"));
        this.tunnel = tag.contains("Tunnel") ? Direction.from2DDataValue(tag.getInt("Tunnel")) : null;
        this.tunnelLength = tag.getInt("TunnelLength");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("MST", type.ordinal());
        tag.putInt("CX", centerX);
        tag.putInt("CZ", centerZ);
        tag.putInt("Bottom", bottom);
        tag.putInt("RoomTop", roomTop);
        tag.putInt("Top", top);
        tag.putInt("Ladder", ladderWall.get2DDataValue());
        if (tunnel != null) {
            tag.putInt("Tunnel", tunnel.get2DDataValue());
        }
        tag.putInt("TunnelLength", tunnelLength);
    }

    private static BoundingBox bounds(int cx, int cz, int bottom, int top, @Nullable Direction tunnel, int length) {
        BoundingBox box = new BoundingBox(cx - 2, bottom, cz - 2, cx + 2, top + 5, cz + 2);
        if (tunnel != null) {
            int reach = TUNNEL_SEARCH_START + length + 1;
            box.encapsulate(new BlockPos(cx + tunnel.getStepX() * reach + 2, top, cz + tunnel.getStepZ() * reach + 2));
            box.encapsulate(new BlockPos(cx + tunnel.getStepX() * reach - 2, top, cz + tunnel.getStepZ() * reach - 2));
        }
        return box;
    }

    @Nullable
    public static MineshaftEntrance create(MineshaftStructure.Type type, BoundingBox room, ChunkGenerator generator,
                                           LevelHeightAccessor heightAccessor, RandomState randomState, RandomSource random) {
        if (type == MineshaftStructure.Type.MESA) return null;
        int cx = room.getCenter().getX();
        int cz = room.getCenter().getZ();
        int seaLevel = generator.getSeaLevel();

        int surface = ground(generator, heightAccessor, randomState, cx, cz);
        int floor = generator.getBaseHeight(cx, cz, Heightmap.Types.OCEAN_FLOOR_WG, heightAccessor, randomState) - 1;
        if (floor != surface || surface <= seaLevel) return null;

        int lowest = surface;
        for (int dx = -2; dx <= 2; dx += 4) {
            for (int dz = -2; dz <= 2; dz += 4) {
                lowest = Math.min(lowest, ground(generator, heightAccessor, randomState, cx + dx, cz + dz));
            }
        }

        int ceiling = lowest - 2;
        int tunnelFloor = ceiling - 4;
        if (tunnelFloor - room.maxY() >= MIN_SHAFT_HEIGHT) {
            Direction first = Direction.from2DDataValue(random.nextInt(4));
            for (int turn = 0; turn < 4; turn++) {
                Direction dir = Direction.from2DDataValue((first.get2DDataValue() + turn) % 4);
                for (int k = TUNNEL_SEARCH_START; k <= TUNNEL_SEARCH_END; k++) {
                    int x = cx + dir.getStepX() * k;
                    int z = cz + dir.getStepZ() * k;
                    int g = ground(generator, heightAccessor, randomState, x, z);
                    if (g <= tunnelFloor && g > seaLevel) {
                        return new MineshaftEntrance(type, cx, cz, room.minY(), room.maxY(), ceiling,
                                dir.getOpposite(), dir, k - TUNNEL_SEARCH_START);
                    }
                }
            }
        }

        if (surface - room.maxY() < MIN_SHAFT_HEIGHT || random.nextFloat() >= HEADFRAME_CHANCE) return null;
        return new MineshaftEntrance(type, cx, cz, room.minY(), room.maxY(), surface,
                Direction.from2DDataValue(random.nextInt(4)), null, 0);
    }

    private static int ground(ChunkGenerator generator, LevelHeightAccessor heightAccessor, RandomState randomState, int x, int z) {
        return generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState) - 1;
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                            RandomSource chunkRandom, BoundingBox box, ChunkPos chunkPos, BlockPos pivot) {
        RandomSource random = RandomSource.create(Mth.getSeed(centerX, bottom, centerZ));
        BlockState planks = type.getPlanksState();
        BlockState log = type.getWoodState();

        int wallTop = tunnel != null ? top - 1 : top;
        for (int y = roomTop + 1; y <= wallTop; y++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos pos = new BlockPos(centerX + dx, y, centerZ + dz);
                    boolean ring = Math.abs(dx) == 2 || Math.abs(dz) == 2;
                    if (!ring) {
                        place(level, box, pos, Blocks.AIR.defaultBlockState());
                    } else if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                        place(level, box, pos, log);
                    } else {
                        place(level, box, pos, wallBlock(random, planks));
                    }
                }
            }
        }

        BlockPos ladderWallPos = new BlockPos(centerX, 0, centerZ).relative(ladderWall, 2);
        BlockPos ladderPos = new BlockPos(centerX, 0, centerZ).relative(ladderWall, 1);
        for (int y = bottom + 1; y <= roomTop; y++) {
            place(level, box, ladderWallPos.atY(y), log);
        }

        BlockState ladder = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, ladderWall.getOpposite());
        if (tunnel != null) {
            buildTunnelTop(level, box, random, planks, ladder, ladderPos);
        } else {
            buildHeadframe(level, box, planks, log, ladder, ladderPos);
        }
    }

    private void buildTunnelTop(WorldGenLevel level, BoundingBox box, RandomSource random, BlockState planks,
                                BlockState ladder, BlockPos ladderPos) {
        int floor = top - 4;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                place(level, box, new BlockPos(centerX + dx, top, centerZ + dz), planks);
                if (Math.abs(dx) < 2 && Math.abs(dz) < 2) {
                    BlockPos pos = new BlockPos(centerX + dx, floor, centerZ + dz);
                    if (!pos.equals(ladderPos.atY(floor))) {
                        place(level, box, pos, planks);
                    }
                }
            }
        }
        for (int y = bottom + 1; y <= floor + 2; y++) {
            place(level, box, ladderPos.atY(y), ladder);
        }

        Direction side = tunnel.getClockWise();
        BlockPos center = new BlockPos(centerX, floor, centerZ);
        for (int lateral = -1; lateral <= 1; lateral++) {
            for (int y = 1; y <= 3; y++) {
                place(level, box, center.relative(tunnel, 2).relative(side, lateral).above(y), Blocks.AIR.defaultBlockState());
            }
        }

        BlockState fence = type.getFenceState();
        int end = TUNNEL_SEARCH_START + tunnelLength + 1;
        for (int k = 3; k <= end; k++) {
            BlockPos row = center.relative(tunnel, k);
            for (int lateral = -1; lateral <= 1; lateral++) {
                BlockPos floorPos = row.relative(side, lateral);
                if (box.isInside(floorPos) && !level.getBlockState(floorPos).isSolid()
                        && (k < end - 1 || level.getBlockState(floorPos.below()).isSolid())) {
                    place(level, box, floorPos, planks);
                }
                for (int y = 1; y <= 3; y++) {
                    place(level, box, floorPos.above(y), Blocks.AIR.defaultBlockState());
                }
            }
            if (k % 4 == 0 && k < end) {
                place(level, box, row.relative(side, -1).above(1), fence);
                place(level, box, row.relative(side, -1).above(2), fence);
                place(level, box, row.relative(side, 1).above(1), fence);
                place(level, box, row.relative(side, 1).above(2), fence);
                for (int lateral = -1; lateral <= 1; lateral++) {
                    place(level, box, row.relative(side, lateral).above(3), random.nextFloat() < 0.25F ? type.getWoodState() : planks);
                }
                if (random.nextFloat() < 0.15F) {
                    place(level, box, row.relative(side, random.nextBoolean() ? -1 : 1).relative(tunnel, 1).above(3), Blocks.COBWEB.defaultBlockState());
                }
            }
        }
    }

    private void buildHeadframe(WorldGenLevel level, BoundingBox box, BlockState planks, BlockState log,
                                BlockState ladder, BlockPos ladderPos) {
        for (int y = bottom + 1; y <= top; y++) {
            place(level, box, ladderPos.atY(y), ladder);
        }
        for (int y = top + 1; y <= top + 4; y++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    place(level, box, new BlockPos(centerX + dx, y, centerZ + dz), Blocks.AIR.defaultBlockState());
                }
            }
        }

        for (int dx = -2; dx <= 2; dx += 4) {
            for (int dz = -2; dz <= 2; dz += 4) {
                for (int y = top + 1; y <= top + 3; y++) {
                    place(level, box, new BlockPos(centerX + dx, y, centerZ + dz), log);
                }
            }
        }

        int beamY = top + 4;
        for (int i = -2; i <= 2; i++) {
            place(level, box, new BlockPos(centerX + i, beamY, centerZ - 2), horizontal(log, Direction.Axis.X));
            place(level, box, new BlockPos(centerX + i, beamY, centerZ + 2), horizontal(log, Direction.Axis.X));
            if (Math.abs(i) < 2) {
                place(level, box, new BlockPos(centerX - 2, beamY, centerZ + i), horizontal(log, Direction.Axis.Z));
                place(level, box, new BlockPos(centerX + 2, beamY, centerZ + i), horizontal(log, Direction.Axis.Z));
            }
        }
        Direction.Axis beamAxis = ladderWall.getAxis();
        for (int i = -1; i <= 1; i++) {
            BlockPos pos = beamAxis == Direction.Axis.X
                    ? new BlockPos(centerX + i, beamY, centerZ)
                    : new BlockPos(centerX, beamY, centerZ + i);
            place(level, box, pos, horizontal(log, beamAxis));
        }
        place(level, box, new BlockPos(centerX, beamY - 1, centerZ),
                Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));

        BlockState fence = type.getFenceState();
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (side == ladderWall) continue;
            Direction along = side.getClockWise();
            BlockState sideFence = fence
                    .setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(along), true)
                    .setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(along.getOpposite()), true);
            for (int lateral = -1; lateral <= 1; lateral++) {
                BlockPos pos = new BlockPos(centerX, top + 1, centerZ).relative(side, 2).relative(along, lateral);
                place(level, box, pos, sideFence);
            }
        }
    }

    private static BlockState wallBlock(RandomSource random, BlockState planks) {
        float roll = random.nextFloat();
        if (roll < 0.12F) return Blocks.COBBLESTONE.defaultBlockState();
        if (roll < 0.18F) return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        return planks;
    }

    private static BlockState horizontal(BlockState log, Direction.Axis axis) {
        return log.hasProperty(RotatedPillarBlock.AXIS) ? log.setValue(RotatedPillarBlock.AXIS, axis) : log;
    }

    private static void place(WorldGenLevel level, BoundingBox box, BlockPos pos, BlockState state) {
        if (box.isInside(pos)) {
            level.setBlock(pos, state, 2);
        }
    }
}
