package com.evandev.spicedcider.entities.strawgolem.goals;

import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.evandev.spicedcider.entities.strawgolem.StrawGolemSenses;
import com.evandev.spicedcider.mixin.strawgolem.StemBlockAccessor;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GolemGrabGoal extends GolemMoveToBlockGoal {
    @Nullable
    private static Set<Item> validItems;

    private List<ItemEntity> items = List.of();
    @Nullable
    private ItemEntity target;
    private int pickupTicks;
    private boolean pickUp;
    private boolean acquired;
    private boolean stopped;

    public GolemGrabGoal(StrawGolem golem) {
        super(golem);
    }

    private boolean isWanted(ItemEntity entity) {
        return validItems(golem).contains(entity.getItem().getItem()) && !entity.hasPickUpDelay()
                && StrawGolemSenses.canPath(golem, entity.blockPosition());
    }

    private void updateNearbyItems() {
        items = golem.level().getEntitiesOfClass(ItemEntity.class,
                        golem.getBoundingBox().inflate(StrawGolem.SEARCH_RANGE, StrawGolem.SEARCH_RANGE_VERTICAL, StrawGolem.SEARCH_RANGE),
                        entity -> validItems(golem).contains(entity.getItem().getItem()) && !entity.hasPickUpDelay())
                .stream()
                .sorted(Comparator.comparingInt(ItemEntity::getAge).reversed())
                .filter(this::isWanted)
                .toList();
    }

    private boolean hasNearbyItem() {
        if (items.isEmpty() || !items.getFirst().isAlive()) {
            updateNearbyItems();
        }
        return !items.isEmpty();
    }

    @Override
    public void start() {
        stopped = false;
        acquired = false;
        pickUp = false;
        pickupTicks = 0;
        target = items.isEmpty() ? null : items.getFirst();
        moveMobToBlock();
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        golem.setPickupStatus(0);
        stopped = true;
        acquired = false;
        pickUp = false;
        pickupTicks = 0;
    }

    @Override
    public void tick() {
        if (!acquired && (target == null || !target.isAlive())) {
            updateNearbyItems();
            if (items.isEmpty()) {
                stop();
                return;
            }
            target = items.getFirst();
            moveMobToBlock();
        }
        if (target == null) {
            return;
        }
        if (mob.distanceTo(target) >= StrawGolem.REACH && golem.getNavigation().isDone()) {
            moveMobToBlock();
        }
        boolean reachable = StrawGolemSenses.canReach(golem, target.blockPosition());
        if (!acquired && !pickUp && reachable && golem.carryStatus() == 0 && target.isAlive() && !target.hasPickUpDelay() && golem.onGround()) {
            target.setPickUpDelay(40);
            golem.lookAt(target, 50.0F, 50.0F);
            golem.setPickupStatus(target.getItem());
            pickUp = true;
        } else if (acquired || pickUp && target.isAlive() && golem.carryStatus() == 0 && reachable) {
            if (pickupTicks == 5) {
                golem.getNavigation().stop();
            }
            if (pickupTicks == 20) {
                golem.setItemSlot(EquipmentSlot.MAINHAND, target.getItem());
                acquired = true;
                target.setItem(ItemStack.EMPTY);
            } else if (pickupTicks >= 40) {
                stop();
            }
            pickupTicks++;
        } else if (pickupTicks != 0 || pickUp) {
            acquired = false;
            pickUp = false;
            golem.setPickupStatus(0);
            pickupTicks = 0;
            moveMobToBlock();
        }
    }

    @Override
    protected void moveMobToBlock() {
        if (target != null) {
            mob.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 0, speedModifier);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !stopped && (golem.carryStatus() == 0 || pickupTicks <= 40) && (acquired || hasNearbyItem());
    }

    @Override
    public boolean canUse() {
        return golem.carryStatus() == 0 && golem.pickupStatus() == 0 && hasNearbyItem()
                && StrawGolemSenses.canSee(golem, items.getFirst().blockPosition());
    }

    private static Set<Item> validItems(StrawGolem golem) {
        if (validItems != null) {
            return validItems;
        }
        Set<Item> set = new HashSet<>();
        if (!(golem.level() instanceof ServerLevel level)) {
            return set;
        }
        for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.Blocks.STRAW_GOLEM_CROPS)) {
            Block block = holder.value();
            if (block instanceof StemBlock stem) {
                Item fruit = BuiltInRegistries.BLOCK.get(((StemBlockAccessor) stem).spicedcider$getFruit()).asItem();
                if (fruit != Items.AIR) {
                    set.add(fruit);
                }
            } else if (block instanceof CropBlock crop) {
                LootParams.Builder builder = new LootParams.Builder(level)
                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                        .withParameter(LootContextParams.ORIGIN, golem.position());
                crop.getStateForAge(crop.getMaxAge()).getDrops(builder).forEach(drop -> set.add(drop.getItem()));
                set.add(block.asItem());
            } else if (block.asItem() != Items.AIR) {
                set.add(block.asItem());
            }
        }
        set.remove(Items.AIR);
        if (!set.isEmpty()) {
            validItems = set;
        }
        return set;
    }
}
