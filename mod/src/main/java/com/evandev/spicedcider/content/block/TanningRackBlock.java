package com.evandev.spicedcider.content.block;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.block.entity.TanningRackBlockEntity;
import com.evandev.spicedcider.recipe.DryingRecipe;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
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

import java.util.Optional;

public class TanningRackBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<TanningRackBlock> CODEC = simpleCodec(TanningRackBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final ResourceLocation TANNING_ADVANCEMENT = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "spiced_cider/tanning");

    private static final double DRIP_OFFSET = 0.14;
    private static final VoxelShape SHAPE_NS = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    private static final VoxelShape SHAPE_EW = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
    private static final VoxelShape OUTLINE_NS_LOWER = Block.box(0.0, 0.0, 6.0, 16.0, 32.0, 10.0);
    private static final VoxelShape OUTLINE_EW_LOWER = Block.box(6.0, 0.0, 0.0, 10.0, 32.0, 16.0);
    private static final VoxelShape OUTLINE_NS_UPPER = Block.box(0.0, -16.0, 6.0, 16.0, 16.0, 10.0);
    private static final VoxelShape OUTLINE_EW_UPPER = Block.box(6.0, -16.0, 0.0, 10.0, 16.0, 16.0);

    public TanningRackBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean ns = state.getValue(FACING).getAxis() == Direction.Axis.Z;
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) return ns ? OUTLINE_NS_LOWER : OUTLINE_EW_LOWER;
        return ns ? OUTLINE_NS_UPPER : OUTLINE_EW_UPPER;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? SHAPE_NS : SHAPE_EW;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
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
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(lowerPos(pos, state)) instanceof TanningRackBlockEntity rack)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (rack.isEmpty()) {
            Optional<RecipeHolder<DryingRecipe>> recipe = level.getRecipeManager().getRecipeFor(ModRecipeSerializers.DRYING_TYPE.get(), new SingleRecipeInput(stack), level);
            if (recipe.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (!level.isClientSide()) {
                rack.hang(recipe.get(), stack);
                hangEffects(level, pos, player);
                stack.consume(1, player);
                dryIfReady(level, rack);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        DryingRecipe recipe = rack.recipe();
        if (recipe == null || rack.stage() != TanningRackBlockEntity.Stage.HUNG) return keepContents(rack, stack);
        if (!rack.isFull()) {
            if (!ItemStack.isSameItemSameComponents(stack, rack.hung())) return keepContents(rack, stack);
            if (!level.isClientSide()) {
                rack.addOne();
                hangEffects(level, pos, player);
                stack.consume(1, player);
                dryIfReady(level, rack);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        if (recipe.agent().isEmpty() || !recipe.agent().get().test(stack)) return keepContents(rack, stack);
        if (level instanceof ServerLevel serverLevel) {
            startDrying(level, rack, recipe);
            level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 1.0F, 0.9F);
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack.copyWithCount(1)),
                    hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, 8, 0.15, 0.15, 0.15, 0.05);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            stack.consume(1, player);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    private void dryIfReady(Level level, TanningRackBlockEntity rack) {
        DryingRecipe recipe = rack.recipe();
        if (recipe != null && recipe.agent().isEmpty() && rack.isFull()) startDrying(level, rack, recipe);
    }

    private void startDrying(Level level, TanningRackBlockEntity rack, DryingRecipe recipe) {
        rack.setStage(TanningRackBlockEntity.Stage.DRYING);
        level.scheduleTick(rack.getBlockPos(), this, recipe.time());
    }

    private static ItemInteractionResult keepContents(TanningRackBlockEntity rack, ItemStack stack) {
        return stack.isEmpty() || rack.stage() == TanningRackBlockEntity.Stage.FINISHED
                ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                : ItemInteractionResult.CONSUME;
    }

    private static void hangEffects(Level level, BlockPos pos, Player player) {
        level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.BLOCKS, 1.0F, 0.8F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(lowerPos(pos, state)) instanceof TanningRackBlockEntity rack) || rack.isEmpty()
                || rack.stage() == TanningRackBlockEntity.Stage.DRYING) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            boolean finished = rack.stage() == TanningRackBlockEntity.Stage.FINISHED;
            ItemStack taken = rack.takeContents();
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.4F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            if (!taken.isEmpty() && !player.getInventory().add(taken)) player.drop(taken, false);
            if (finished && player instanceof ServerPlayer serverPlayer) awardTanning(serverPlayer);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER || !(level.getBlockEntity(pos) instanceof TanningRackBlockEntity rack)
                || rack.stage() != TanningRackBlockEntity.Stage.DRYING) return;
        rack.setStage(TanningRackBlockEntity.Stage.FINISHED);
        level.playSound(null, pos.above(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.BLOCKS, 0.8F, 1.0F);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TanningRackBlockEntity rack) {
            ItemStack contents = rack.contents();
            if (!contents.isEmpty()) Containers.dropItemStack(level, pos.getX(), pos.getY() + 1, pos.getZ(), contents);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(6) != 0 || !(level.getBlockEntity(lowerPos(pos, state)) instanceof TanningRackBlockEntity rack)
                || rack.stage() != TanningRackBlockEntity.Stage.DRYING) return;
        boolean ns = state.getValue(FACING).getAxis() == Direction.Axis.Z;
        double along = 0.15 + random.nextDouble() * 0.7;
        double across = 0.5 + (random.nextBoolean() ? DRIP_OFFSET : -DRIP_OFFSET);
        double x = pos.getX() + (ns ? along : across);
        double z = pos.getZ() + (ns ? across : along);
        double y = state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0.65 + random.nextDouble() * 0.3 : random.nextDouble() * 0.7;
        level.addParticle(ParticleTypes.FALLING_HONEY, x, pos.getY() + y, z, 0.0, 0.0, 0.0);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new TanningRackBlockEntity(pos, state) : null;
    }

    private static void awardTanning(ServerPlayer player) {
        AdvancementHolder holder = player.server.getAdvancements().get(TANNING_ADVANCEMENT);
        if (holder != null) player.getAdvancements().award(holder, "tanned_hide");
    }

    private static BlockPos lowerPos(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
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
}
