package com.evandev.spicedcider.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MetalLadderBlock extends LadderBlock {
    private static final VoxelShape POLE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    private static final int MAX_EXTEND = 32;

    public MetalLadderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean supportsExternalFaceHiding(BlockState state) {
        return false;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacent, Direction direction) {
        if (direction != null && direction.getAxis().isHorizontal()) return adjacent.isAir() || !adjacent.blocksMotion();
        return direction == Direction.UP && adjacent.getBlock() instanceof LadderBlock;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return POLE;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return super.canSurvive(state, level, pos) || above.is(this) && above.getValue(FACING) == state.getValue(FACING);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown() || !player.mayBuild() || !stack.is(asItem())) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        BlockPos target = pos.below();
        for (int i = 0; i < MAX_EXTEND && level.getBlockState(target).is(this); i++) target = target.below();
        BlockState existing = level.getBlockState(target);
        if (!existing.canBeReplaced() || level.isOutsideBuildHeight(target)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        BlockState placed = state.setValue(WATERLOGGED, level.getFluidState(target).getType() == Fluids.WATER);
        if (!level.isClientSide()) {
            level.setBlock(target, placed, Block.UPDATE_ALL);
            SoundType sound = placed.getSoundType(level, target, player);
            level.playSound(null, target, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, target);
            stack.consume(1, player);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }
}
