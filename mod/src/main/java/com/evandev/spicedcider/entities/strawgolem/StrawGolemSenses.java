package com.evandev.spicedcider.entities.strawgolem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.pathfinder.Path;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.BiPredicate;

public class StrawGolemSenses {

    public static boolean canSee(Entity entity, BlockPos pos) {
        if (Math.abs(entity.getY() - pos.getY()) > StrawGolem.SEARCH_RANGE_VERTICAL) {
            return false;
        }
        double dx = entity.getX() - pos.getX();
        double dz = entity.getZ() - pos.getZ();
        return dx * dx + dz * dz <= StrawGolem.SEARCH_RANGE * StrawGolem.SEARCH_RANGE;
    }

    public static boolean canPath(Mob mob, BlockPos pos) {
        Path path = mob.getNavigation().createPath(pos, 1);
        return path != null && path.canReach();
    }

    public static boolean canReach(Mob mob, BlockPos pos) {
        return pos.closerToCenterThan(mob.position(), StrawGolem.REACH);
    }

    @Nullable
    public static IItemHandler storage(Level level, @Nullable BlockPos pos) {
        if (pos == null || !level.isLoaded(pos)) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AbstractFurnaceBlockEntity || blockEntity instanceof BrewingStandBlockEntity
                || blockEntity instanceof JukeboxBlockEntity || blockEntity instanceof CrafterBlockEntity) {
            return null;
        }
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
    }

    public static boolean isStorage(Level level, @Nullable BlockPos pos) {
        return storage(level, pos) != null;
    }

    public static boolean accepts(Level level, @Nullable BlockPos pos, ItemStack stack) {
        IItemHandler handler = storage(level, pos);
        return handler != null && (stack.isEmpty() || ItemHandlerHelper.insertItemStacked(handler, stack.copy(), true).getCount() < stack.getCount());
    }

    public static ItemStack insert(Level level, BlockPos pos, ItemStack stack) {
        IItemHandler handler = storage(level, pos);
        return handler == null ? stack : ItemHandlerHelper.insertItemStacked(handler, stack.copy(), false);
    }

    @Nullable
    public static BlockPos findNearestBlock(StrawGolem golem, BiPredicate<StrawGolem, BlockPos> test) {
        BlockPos closest = null;
        BlockPos origin = golem.blockPosition();
        for (BlockPos pos : area(origin)) {
            if ((closest == null || origin.distManhattan(pos) < origin.distManhattan(closest)) && test.test(golem, pos)) {
                closest = pos.immutable();
            }
        }
        return closest;
    }

    public static Queue<BlockPos> nearbyBlocks(StrawGolem golem, BiPredicate<StrawGolem, BlockPos> test) {
        BlockPos origin = golem.blockPosition();
        Queue<BlockPos> queue = new PriorityQueue<>(Comparator.comparingInt(pos -> pos.distManhattan(origin)));
        for (BlockPos pos : area(origin)) {
            if (test.test(golem, pos)) {
                queue.add(pos.immutable());
            }
        }
        return queue;
    }

    private static Iterable<BlockPos> area(BlockPos origin) {
        int range = StrawGolem.SEARCH_RANGE;
        int vertical = StrawGolem.SEARCH_RANGE_VERTICAL;
        return BlockPos.betweenClosed(origin.offset(-range, -vertical, -range), origin.offset(range, vertical, range));
    }
}
