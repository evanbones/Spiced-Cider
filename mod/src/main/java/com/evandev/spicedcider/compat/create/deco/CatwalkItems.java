package com.evandev.spicedcider.compat.create.deco;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Predicate;

public class CatwalkItems {
    private static int catwalkHelper = -1;
    private static int stairHelper = -1;
    private static int railingHelper = -1;

    private static int catwalkHelper() {
        if (catwalkHelper < 0) catwalkHelper = PlacementHelpers.register(new CatwalkHelper());
        return catwalkHelper;
    }

    private static int stairHelper() {
        if (stairHelper < 0) stairHelper = PlacementHelpers.register(new StairHelper());
        return stairHelper;
    }

    private static int railingHelper() {
        if (railingHelper < 0) railingHelper = PlacementHelpers.register(new RailingHelper());
        return railingHelper;
    }

    private static void playPlaceSound(Level level, Player player, BlockPos pos, BlockState state) {
        SoundType sound = state.getSoundType();
        level.playSound(player, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, state));
    }

    public static class Catwalk extends BlockItem {
        private final int helper;

        public Catwalk(Block block, Properties properties) {
            super(block, properties);
            helper = catwalkHelper();
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            IPlacementHelper placement = PlacementHelpers.get(helper);
            Player player = context.getPlayer();
            if (player != null && !player.isShiftKeyDown() && placement.matchesState(state)) {
                BlockHitResult ray = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, true);
                return placement.getOffset(player, level, state, pos, ray).placeInWorld(level, this, player, context.getHand(), ray).result();
            }
            return super.useOn(context);
        }
    }

    public static class Stairs extends BlockItem {
        private final int helper;

        public Stairs(Block block, Properties properties) {
            super(block, properties);
            helper = stairHelper();
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            IPlacementHelper placement = PlacementHelpers.get(helper);
            Player player = context.getPlayer();
            if (player != null && !player.isShiftKeyDown() && placement.matchesState(state)) {
                BlockHitResult ray = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, true);
                return placement.getOffset(player, level, state, pos, ray).placeInWorld(level, this, player, context.getHand(), ray).result();
            }
            return super.useOn(context);
        }
    }

    public static class Railing extends BlockItem {
        private final int helper;

        public Railing(Block block, Properties properties) {
            super(block, properties);
            helper = railingHelper();
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            ItemStack stack = context.getItemInHand();
            BlockState state = level.getBlockState(pos);
            if (player == null) return InteractionResult.PASS;

            if (state.getBlock() instanceof CatwalkStairBlock stair && stair.getRailing() == getBlock()) {
                boolean left = CatwalkStairBlock.isLeftSide(state, context.getClickLocation().subtract(pos.getCenter()));
                BooleanProperty side = left ? CatwalkStairBlock.RAILING_LEFT : CatwalkStairBlock.RAILING_RIGHT;
                if (state.getValue(side)) return InteractionResult.PASS;
                BlockState placed = state.setValue(side, true);
                level.setBlock(pos, placed, Block.UPDATE_ALL);
                playPlaceSound(level, player, pos, placed);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            IPlacementHelper placement = PlacementHelpers.get(helper);
            if (!player.isShiftKeyDown() && placement.matchesState(state)) {
                BlockHitResult ray = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, true);
                PlacementOffset offset = placement.getOffset(player, level, state, pos, ray);
                if (offset.isSuccessful()) {
                    BlockState placed = offset.getTransform().apply(offset.getGhostState());
                    level.setBlock(offset.getBlockPos(), placed, Block.UPDATE_ALL);
                    playPlaceSound(level, player, offset.getBlockPos(), placed);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
            return super.useOn(context);
        }
    }

    private static class CatwalkHelper implements IPlacementHelper {
        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return CatwalkBlock::isCatwalk;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> CatwalkBlock.isCatwalk(state.getBlock());
        }

        @Override
        public PlacementOffset getOffset(Player player, Level level, BlockState state, BlockPos pos, BlockHitResult ray) {
            Direction face = ray.getDirection();
            if (face.getAxis() != Direction.Axis.Y) {
                return PlacementOffset.success(pos.relative(face), placed -> placed);
            }
            for (Direction direction : IPlacementHelper.orderedByDistanceExceptAxis(pos, ray.getLocation(), Direction.Axis.Y)) {
                BlockPos target = pos.relative(direction);
                if (CatwalkBlock.canPlaceCatwalk(level, target)) {
                    return PlacementOffset.success(target, placed -> placed);
                }
            }
            return PlacementOffset.fail();
        }
    }

    private static class StairHelper implements IPlacementHelper {
        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return CatwalkStairBlock::isCatwalkStair;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> CatwalkBlock.isCatwalk(state.getBlock());
        }

        @Override
        public PlacementOffset getOffset(Player player, Level level, BlockState state, BlockPos pos, BlockHitResult ray) {
            for (Direction direction : IPlacementHelper.orderedByDistanceExceptAxis(pos, ray.getLocation(), Direction.Axis.Y)) {
                Direction facing;
                if (state.hasProperty(CatwalkStairBlock.FACING)) {
                    facing = state.getValue(CatwalkStairBlock.FACING);
                    if (direction.getOpposite() != facing) continue;
                } else {
                    facing = direction.getOpposite();
                }
                BlockPos target = pos.relative(direction).above();
                if (!CatwalkBlock.canPlaceCatwalk(level, target)) continue;
                return PlacementOffset.success(target, placed -> placed.hasProperty(CatwalkStairBlock.FACING)
                        ? placed.setValue(CatwalkStairBlock.FACING, facing) : placed);
            }
            return PlacementOffset.fail();
        }
    }

    private static class RailingHelper implements IPlacementHelper {
        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return CatwalkRailingBlock::isRailing;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> true;
        }

        @Override
        public PlacementOffset getOffset(Player player, Level level, BlockState state, BlockPos pos, BlockHitResult ray) {
            BlockState adjacent = level.getBlockState(pos.relative(ray.getDirection()));
            if (adjacent.getBlock() instanceof CatwalkRailingBlock) {
                pos = pos.relative(ray.getDirection());
                state = adjacent;
            }
            if (!(state.getBlock() instanceof CatwalkRailingBlock) || CatwalkRailingBlock.sideCount(state) == 4
                    || !player.isHolding(state.getBlock().asItem())) {
                return PlacementOffset.fail();
            }
            for (Direction direction : IPlacementHelper.orderedByDistanceExceptAxis(pos, ray.getLocation(), Direction.Axis.Y)) {
                BooleanProperty side = CatwalkRailingBlock.sideProperty(direction);
                if (!state.getValue(side)) {
                    BlockState result = state.setValue(side, true);
                    return PlacementOffset.success(pos, placed -> result).withGhostState(result);
                }
            }
            return PlacementOffset.fail();
        }
    }
}
