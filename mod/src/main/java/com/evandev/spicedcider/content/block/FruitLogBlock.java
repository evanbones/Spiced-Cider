package com.evandev.spicedcider.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FruitLogBlock extends RotatedPillarBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<FruitLogBlock> CODEC = simpleCodec(properties -> new FruitLogBlock(() -> null, properties));
    public static final BooleanProperty LEAFY = BooleanProperty.create("leafy");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
    private static final Map<Direction, VoxelShape> ARMS = new EnumMap<>(Map.of(
            Direction.UP, Block.box(4, 12, 4, 12, 16, 12),
            Direction.DOWN, Block.box(4, 0, 4, 12, 4, 12),
            Direction.NORTH, Block.box(4, 4, 0, 12, 12, 4),
            Direction.SOUTH, Block.box(4, 4, 12, 12, 12, 16),
            Direction.WEST, Block.box(0, 4, 4, 4, 12, 12),
            Direction.EAST, Block.box(12, 4, 4, 16, 12, 12)));

    private final Supplier<? extends Block> stripped;
    private final Map<BlockState, VoxelShape> shapes = new HashMap<>();

    public FruitLogBlock(Supplier<? extends Block> stripped, Properties properties) {
        super(properties);
        this.stripped = stripped;
        BlockState state = this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(LEAFY, false).setValue(WATERLOGGED, false);
        for (BooleanProperty property : PipeBlock.PROPERTY_BY_DIRECTION.values()) state = state.setValue(property, false);
        this.registerDefaultState(state);
        for (BlockState possible : this.stateDefinition.getPossibleStates()) this.shapes.put(possible, computeShape(possible));
    }

    @Override
    public @NotNull MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PipeBlock.NORTH, PipeBlock.EAST, PipeBlock.SOUTH, PipeBlock.WEST, PipeBlock.UP, PipeBlock.DOWN, LEAFY, WATERLOGGED);
    }

    public static boolean hasArm(BlockState state, Direction direction) {
        if (state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction))) return true;
        Direction.Axis axis = state.getValue(AXIS);
        if (direction.getAxis() != axis) return false;
        if (!state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction.getOpposite()))) return true;
        for (Direction other : Direction.values()) {
            if (other.getAxis() != axis && state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(other))) return false;
        }
        return true;
    }

    private static VoxelShape computeShape(BlockState state) {
        if (state.getValue(LEAFY)) return Shapes.block();
        VoxelShape shape = CORE;
        for (Direction direction : Direction.values()) {
            if (hasArm(state, direction)) shape = Shapes.or(shape, ARMS.get(direction));
        }
        return shape.optimize();
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.shapes.get(state);
    }

    public static BlockState connect(BlockState state, BlockGetter level, BlockPos pos) {
        int leaves = 0;
        for (Direction direction : Direction.values()) {
            BlockState neighbor = level.getBlockState(pos.relative(direction));
            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), neighbor.getBlock() instanceof FruitLogBlock);
            if (neighbor.is(BlockTags.LEAVES) || neighbor.getBlock() instanceof FruitLogBlock && neighbor.getValue(LEAFY)) leaves++;
        }
        return state.setValue(LEAFY, leaves > 2);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        return connect(state, context.getLevel(), context.getClickedPos())
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
                                              @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return connect(state, level, pos);
    }

    @Override
    protected @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType type) {
        return false;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(@NotNull BlockState state, @NotNull UseOnContext context, @NotNull ItemAbility ability, boolean simulate) {
        Block strippedBlock = this.stripped.get();
        if (ability == ItemAbilities.AXE_STRIP && strippedBlock != null) return strippedBlock.withPropertiesOf(state);
        return super.getToolModifiedState(state, context, ability, simulate);
    }
}
