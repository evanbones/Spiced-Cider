package com.evandev.spicedcider.content.casting;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CrucibleBlock extends BaseEntityBlock {
    public static final MapCodec<CrucibleBlock> CODEC = simpleCodec(CrucibleBlock::new);
    public static final BooleanProperty SEATED = BooleanProperty.create("seated");
    public static final BooleanProperty SHALLOW = BooleanProperty.create("shallow");
    public static final TagKey<Block> SEATS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "crucible_seats"));
    public static final TagKey<Block> SHALLOW_SEATS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "crucible_shallow_seats"));
    public static final float SEAT_DROP = 13.0F / 16.0F;
    public static final float SHALLOW_SEAT_DROP = 8.0F / 16.0F;
    private static final VoxelShape SHAPE = Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 12.0, 11.0), Block.box(4.5, 12.0, 4.5, 11.5, 14.0, 11.5));
    private static final VoxelShape SEATED_SHAPE = SHAPE.move(0.0, -SEAT_DROP, 0.0);
    private static final VoxelShape SHALLOW_SHAPE = SHAPE.move(0.0, -SHALLOW_SEAT_DROP, 0.0);

    public CrucibleBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(SEATED, false).setValue(SHALLOW, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SEATED, SHALLOW);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return seat(defaultBlockState(), context.getLevel().getBlockState(context.getClickedPos().below()));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN ? seat(state, neighbor) : state;
    }

    public static boolean isSeat(BlockState below) {
        return below.is(SEATS) || below.is(SHALLOW_SEATS);
    }

    private static BlockState seat(BlockState state, BlockState below) {
        boolean shallow = below.is(SHALLOW_SEATS);
        return state.setValue(SEATED, shallow || below.is(SEATS)).setValue(SHALLOW, shallow);
    }

    public static float drop(BlockState state) {
        if (!state.hasProperty(SEATED) || !state.getValue(SEATED)) return 0.0F;
        return state.getValue(SHALLOW) ? SHALLOW_SEAT_DROP : SEAT_DROP;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.getValue(SEATED)) return SHAPE;
        return state.getValue(SHALLOW) ? SHALLOW_SHAPE : SEATED_SHAPE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.CRUCIBLE.get(), level.isClientSide() ? CrucibleBlockEntity::clientTick : CrucibleBlockEntity::serverTick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        FluidStack fluid = crucible.getFluid();
        if (stack.is(Items.BUCKET)) {
            if (fluid.getAmount() < CrucibleBlockEntity.CAPACITY || fluid.getFluid().getBucket() == Items.AIR) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide()) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(fluid.getFluid().getBucket())));
                crucible.setFluid(FluidStack.EMPTY);
                level.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        if (stack.getItem() instanceof BucketItem bucket && bucket.content != Fluids.EMPTY && fluid.isEmpty()
                && Casting.castingRecipeExists(level, new FluidStack(bucket.content, CrucibleBlockEntity.CAPACITY))) {
            if (!level.isClientSide()) {
                crucible.setFluid(new FluidStack(bucket.content, CrucibleBlockEntity.CAPACITY));
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        if (Casting.isMeltable(level, stack)) {
            if (!crucible.insert(stack)) {
                return ItemInteractionResult.CONSUME;
            }
            if (!level.isClientSide()) {
                stack.consume(1, player);
                level.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 0.8F, 0.9F + level.random.nextFloat() * 0.2F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible)) {
            return InteractionResult.PASS;
        }
        if (player.isSecondaryUseActive() && player.getMainHandItem().isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack carried = new ItemStack(this);
                carried.applyComponents(crucible.collectComponents());
                level.removeBlockEntity(pos);
                level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                player.setItemInHand(InteractionHand.MAIN_HAND, carried);
                level.playSound(null, pos, SoundEvents.DECORATED_POT_HIT, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (!crucible.hasInputs()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            ItemStack taken = crucible.takeLast();
            if (!player.getInventory().add(taken)) {
                player.drop(taken, false);
            }
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.3F, 1.1F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible && !crucible.getFluid().isEmpty()
                && CastingHeat.below(level, pos) != CastingHeat.NONE && random.nextInt(6) == 0) {
            level.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 0.8 - drop(state), pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible) {
            return Math.round(15.0F * crucible.getFluid().getAmount() / CrucibleBlockEntity.CAPACITY);
        }
        return 0;
    }
}
