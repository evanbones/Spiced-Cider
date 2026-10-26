package com.evandev.spicedcider.content.block;

import com.evandev.spicedcider.content.block.entity.TubewormBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TubewormBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HIDING = BooleanProperty.create("hiding");
    public static final BooleanProperty SHEARED = BooleanProperty.create("sheared");
    public static final EnumProperty<TubewormPart> PART = EnumProperty.create("part", TubewormPart.class);
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public TubewormBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, true)
                .setValue(HIDING, false)
                .setValue(SHEARED, false)
                .setValue(PART, TubewormPart.TOP)
                .setValue(COLOR, DyeColor.RED));
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return state.getValue(SHEARED);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(SHEARED) && random.nextInt(5) == 0) {
            level.setBlockAndUpdate(pos, state.setValue(SHEARED, false));
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, HIDING, SHEARED, PART, COLOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockState blockAbove = context.getLevel().getBlockState(context.getClickedPos().above());
        boolean isTubewormAbove = blockAbove.getBlock() instanceof TubewormBlock;

        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(PART, isTubewormAbove ? TubewormPart.MIDDLE : TubewormPart.TOP);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (direction == Direction.UP) {
            boolean isTubewormAbove = neighborState.getBlock() instanceof TubewormBlock;
            return state.setValue(PART, isTubewormAbove ? TubewormPart.MIDDLE : TubewormPart.TOP);
        }

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (stack.getItem() instanceof ShearsItem) {
            if (state.getValue(PART) == TubewormPart.TOP && !state.getValue(SHEARED)) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, state.setValue(SHEARED, true));
                    stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    popResource(level, pos, new ItemStack(DyeItem.byColor(state.getValue(COLOR)), 4));
                    level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        } else if (stack.getItem() instanceof DyeItem dyeItem) {
            if (state.getValue(COLOR) != dyeItem.getDyeColor()) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, state.setValue(COLOR, dyeItem.getDyeColor()));
                    if (!player.isCreative()) stack.shrink(1);
                    level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TubewormBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return !level.isClientSide ? TubewormBlockEntity::tick : null;
    }
}