package com.evandev.spicedcider.content.block;

import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TanningRackBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<TanningRackBlock> CODEC = simpleCodec(TanningRackBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<Contents> CONTENTS = EnumProperty.create("contents", Contents.class);
    public static final int TANNING_TICKS = 2400;
    public static final int LARGE_HIDE_LEATHER = 3;
    private static final ResourceLocation HOGLIN_HIDE = ResourceLocation.fromNamespaceAndPath("mynethersdelight", "hoglin_hide");

    private static final VoxelShape SHAPE_NS = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    private static final VoxelShape SHAPE_EW = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public TanningRackBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(CONTENTS, Contents.EMPTY));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, CONTENTS);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? SHAPE_NS : SHAPE_EW;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() >= level.getMaxBuildHeight() - 1 || !level.getBlockState(pos.above()).canBeReplaced(context)) return null;
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) return true;
        BlockState below = level.getBlockState(pos.below());
        return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP)) {
            return neighbor.is(this) && neighbor.getValue(HALF) != half ? state : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos below = pos.below();
            BlockState lower = level.getBlockState(below);
            if (lower.is(this) && lower.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(below, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, below, Block.getId(lower));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Contents contents = state.getValue(CONTENTS);
        boolean large = stack.is(ModTags.Items.LARGE_TANNABLE_HIDES);
        if (contents == Contents.EMPTY && (large || stack.is(ModTags.Items.TANNABLE_HIDES))) {
            if (!level.isClientSide()) {
                setContents(level, pos, state, large ? Contents.HOGLIN_HIDE : Contents.HIDE);
                level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.BLOCKS, 1.0F, 0.8F);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                stack.consume(1, player);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        if ((contents == Contents.HIDE || contents == Contents.HOGLIN_HIDE) && stack.is(ModTags.Items.TANNING_AGENTS)) {
            if (level instanceof ServerLevel serverLevel) {
                BlockPos lower = lowerPos(pos, state);
                setContents(level, pos, state, contents == Contents.HIDE ? Contents.TANNING : Contents.HOGLIN_TANNING);
                level.scheduleTick(lower, this, TANNING_TICKS);
                level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 1.0F, 0.9F);
                serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack.copyWithCount(1)),
                        hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, 8, 0.15, 0.15, 0.15, 0.05);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                stack.consume(1, player);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        Contents contents = state.getValue(CONTENTS);
        ItemStack taken = switch (contents) {
            case HIDE -> new ItemStack(ModItems.HIDE.get());
            case LEATHER -> new ItemStack(Items.LEATHER);
            case HOGLIN_HIDE -> new ItemStack(BuiltInRegistries.ITEM.get(HOGLIN_HIDE));
            case HOGLIN_LEATHER -> new ItemStack(Items.LEATHER, LARGE_HIDE_LEATHER);
            default -> ItemStack.EMPTY;
        };
        if (taken.isEmpty()) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            setContents(level, pos, state, Contents.EMPTY);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.4F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            if (!player.getInventory().add(taken)) player.drop(taken, false);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER || !state.getValue(CONTENTS).isTanning()) return;
        setContents(level, pos, state, state.getValue(CONTENTS) == Contents.TANNING ? Contents.LEATHER : Contents.HOGLIN_LEATHER);
        level.playSound(null, pos.above(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.BLOCKS, 0.8F, 1.0F);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(CONTENTS).isTanning() || random.nextInt(6) != 0) return;
        boolean ns = state.getValue(FACING).getAxis() == Direction.Axis.Z;
        double along = 0.15 + random.nextDouble() * 0.7;
        double x = pos.getX() + (ns ? along : 0.5);
        double z = pos.getZ() + (ns ? 0.5 : along);
        level.addParticle(ParticleTypes.FALLING_HONEY, x, pos.getY() + 0.1 + random.nextDouble() * 0.6, z, 0.0, 0.0, 0.0);
    }

    private static BlockPos lowerPos(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
    }

    private void setContents(Level level, BlockPos pos, BlockState state, Contents contents) {
        BlockPos lower = lowerPos(pos, state);
        BlockState lowerState = level.getBlockState(lower);
        BlockState upperState = level.getBlockState(lower.above());
        if (lowerState.is(this)) level.setBlock(lower, lowerState.setValue(CONTENTS, contents), Block.UPDATE_ALL);
        if (upperState.is(this)) level.setBlock(lower.above(), upperState.setValue(CONTENTS, contents), Block.UPDATE_ALL);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    public enum Contents implements StringRepresentable {
        EMPTY("empty"),
        HIDE("hide"),
        TANNING("tanning"),
        LEATHER("leather"),
        HOGLIN_HIDE("hoglin_hide"),
        HOGLIN_TANNING("hoglin_tanning"),
        HOGLIN_LEATHER("hoglin_leather");

        private final String name;

        Contents(String name) {
            this.name = name;
        }

        public boolean isTanning() {
            return this == TANNING || this == HOGLIN_TANNING;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
