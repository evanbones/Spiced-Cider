package com.evandev.spicedcider.entities.strawgolem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class StrawGolemDeliverer {
    private static final int SEARCH_COOLDOWN = 40;

    private final StrawGolem golem;
    private final BiPredicate<StrawGolem, BlockPos> predicate = (golem, pos) -> StrawGolemSenses.canSee(golem, pos)
            && StrawGolemSenses.accepts(golem.level(), pos, golem.getMainHandItem())
            && StrawGolemSenses.canPath(golem, pos);
    @Nullable
    private BlockPos storagePos;
    private int nextSearch;

    public StrawGolemDeliverer(StrawGolem golem) {
        this.golem = golem;
    }

    public boolean isValid(@Nullable BlockPos pos) {
        return pos != null && predicate.test(golem, pos);
    }

    public boolean isUsable(@Nullable BlockPos pos) {
        return pos != null && StrawGolemSenses.canSee(golem, pos) && StrawGolemSenses.accepts(golem.level(), pos, golem.getMainHandItem());
    }

    public boolean shouldChangeDeliverable(BlockPos pos) {
        BlockPos priority = golem.getPriorityPos();
        return priority != null && !pos.equals(priority) && isValid(priority);
    }

    @Nullable
    public BlockPos getDeliverable() {
        BlockPos priority = golem.getPriorityPos();
        if (isValid(priority)) {
            return priority;
        }
        if (isValid(storagePos)) {
            return storagePos;
        }
        if (golem.tickCount < nextSearch) {
            return null;
        }
        nextSearch = golem.tickCount + SEARCH_COOLDOWN + golem.getRandom().nextInt(20);
        storagePos = StrawGolemSenses.findNearestBlock(golem, predicate);
        return storagePos;
    }

    public void deliver(BlockPos pos) {
        ItemStack held = golem.getMainHandItem();
        if (held.isEmpty()) {
            return;
        }
        golem.setItemSlot(EquipmentSlot.MAINHAND, StrawGolemSenses.insert(golem.level(), pos, held));
    }
}
