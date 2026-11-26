package com.evandev.spicedcider.content.block.lead;

import com.evandev.spicedcider.entities.leadweight.LeadWeightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LeadWeightItem extends BlockItem {
    private static final double NEAR_VERTICAL = 0.1;
    private static final int DROP_COOLDOWN = 10;

    public LeadWeightItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockPos cell = targetCell(level, player, player.getViewVector(1.0F));
        if (cell == null) {
            return InteractionResultHolder.pass(stack);
        }
        if (level instanceof ServerLevel serverLevel) {
            if (FallingBlock.isFree(level.getBlockState(cell.below()))) {
                LeadWeightEntity.inAir(serverLevel, cell.getX() + 0.5, cell.getY(), cell.getZ() + 0.5, getBlock().defaultBlockState(), player);
            } else {
                placeResting(serverLevel, cell);
            }
        }
        player.swing(hand, true);
        stack.consume(1, player);
        player.getCooldowns().addCooldown(this, DROP_COOLDOWN);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void placeResting(ServerLevel level, BlockPos cell) {
        BlockState state = getBlock().defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(cell).is(Fluids.WATER));
        level.setBlock(cell, state, Block.UPDATE_ALL);
        SoundType sound = state.getSoundType();
        level.playSound(null, cell, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
    }

    @Nullable
    private static BlockPos targetCell(Level level, Player player, Vec3 look) {
        BlockPos feet = player.blockPosition();
        double horizontal = Math.sqrt(look.x * look.x + look.z * look.z);
        if (horizontal < NEAR_VERTICAL && look.y < 0 && level.getBlockState(feet.below()).canBeReplaced()) {
            return feet.below();
        }
        int[] dir = aimDirection(player.getDirection(), look, horizontal);
        int dy = horizontal < NEAR_VERTICAL ? (look.y > 0 ? 2 : 0) : Mth.clamp(1 + (int) Math.round(look.y / horizontal), 0, 2);
        BlockPos cell = feet.offset(dir[0], dy, dir[1]);
        if (level.getBlockState(cell).canBeReplaced()) {
            return cell;
        }
        BlockPos above = cell.above();
        return level.getBlockState(above).canBeReplaced() ? above : null;
    }

    private static int[] aimDirection(Direction facing, Vec3 look, double horizontal) {
        if (horizontal < NEAR_VERTICAL) {
            return new int[]{facing.getStepX(), facing.getStepZ()};
        }
        int bestX = 0, bestZ = 0;
        double bestDot = -Double.MAX_VALUE;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                double dot = (dx * look.x + dz * look.z) / Math.sqrt(dx * dx + dz * dz);
                if (dot > bestDot) {
                    bestDot = dot;
                    bestX = dx;
                    bestZ = dz;
                }
            }
        }
        return new int[]{bestX, bestZ};
    }

    public static class DispenseBehavior extends OptionalDispenseItemBehavior {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            setSuccess(false);
            if (stack.getItem() instanceof BlockItem blockItem) {
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos target = source.pos().relative(facing);
                Direction placedAgainst = source.level().isEmptyBlock(target.below()) ? facing : Direction.UP;
                setSuccess(blockItem.place(new DirectionalPlaceContext(source.level(), target, facing, stack, placedAgainst)).consumesAction());
            }
            return stack;
        }
    }
}
