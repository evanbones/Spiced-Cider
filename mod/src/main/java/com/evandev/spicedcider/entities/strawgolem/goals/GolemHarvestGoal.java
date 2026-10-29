package com.evandev.spicedcider.entities.strawgolem.goals;

import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.evandev.spicedcider.entities.strawgolem.StrawGolemSenses;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.function.BiPredicate;

public class GolemHarvestGoal extends GolemMoveToBlockGoal {
    private static final int SEARCH_COOLDOWN = 40;

    private final BiPredicate<StrawGolem, BlockPos> predicate = (golem, pos) -> isGrownPlant(golem.level(), pos) && StrawGolemSenses.canPath(golem, pos);
    @Nullable
    private Queue<BlockPos> queue;
    private int harvestTimer;
    @Nullable
    private ItemStack item;
    private int nextSearch;

    public GolemHarvestGoal(StrawGolem golem) {
        super(golem);
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return StrawGolemSenses.canSee(golem, pos) && predicate.test(golem, pos);
    }

    @Override
    public void tick() {
        super.tick();
        if (item == null && StrawGolemSenses.canReach(mob, blockPos)) {
            item = harvest();
            golem.setPickupStatus(item);
            golem.getNavigation().stop();
        } else if (item != null && !item.isEmpty()) {
            golem.getLookControl().setLookAt(blockPos.getCenter().x, blockPos.getCenter().y, blockPos.getCenter().z, 180, 180);
            harvestTimer++;
            if (harvestTimer == 20 && predicate.test(golem, blockPos) && StrawGolemSenses.canReach(mob, blockPos)) {
                item = harvest();
                resetBlock(golem.level());
                golem.setItemSlot(EquipmentSlot.MAINHAND, item);
            } else if (harvestTimer == 40) {
                golem.setPickupStatus(0);
            } else if (harvestTimer < 20 && !predicate.test(golem, blockPos)) {
                golem.setPickupStatus(0);
                item = null;
            }
        } else if (shouldRecalculatePath() && golemCollision()) {
            nudge();
        }
    }

    @Override
    public void start() {
        moveMobToBlock();
        tryTicks = 0;
        harvestTimer = 0;
        item = null;
    }

    @Override
    public void stop() {
        golem.setPickupStatus(0);
        golem.getNavigation().stop();
    }

    @Override
    public boolean canUse() {
        if (!golem.getMainHandItem().isEmpty()) {
            return false;
        }
        if (queue == null || queue.isEmpty()) {
            if (golem.tickCount < nextSearch) {
                return false;
            }
            nextSearch = golem.tickCount + SEARCH_COOLDOWN + golem.getRandom().nextInt(20);
            queue = StrawGolemSenses.nearbyBlocks(golem, (golem, pos) -> StrawGolemSenses.canSee(golem, pos) && isGrownPlant(golem.level(), pos));
        }
        while (!queue.isEmpty()) {
            BlockPos next = queue.poll();
            if (isValidTarget(golem.level(), next)) {
                blockPos = next;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return harvestTimer <= 38 && isValidTarget(golem.level(), blockPos);
    }

    private static boolean isGrownPlant(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }
        if (block instanceof BushBlock && !(block instanceof StemBlock)) {
            for (Property<?> property : state.getProperties()) {
                if (property instanceof IntegerProperty age && property.getName().equals("age")
                        && state.getValue(age) >= Collections.max(age.getPossibleValues())) {
                    return true;
                }
            }
            return false;
        }
        return (block == Blocks.PUMPKIN || block == Blocks.MELON) && attachedStem(level, pos) != null;
    }

    @Nullable
    private static Direction attachedStem(Level level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockState stem = level.getBlockState(pos.relative(dir));
            if (stem.getBlock() instanceof AttachedStemBlock && stem.getValue(AttachedStemBlock.FACING) == dir.getOpposite()) {
                return dir;
            }
        }
        return null;
    }

    private ItemStack harvest() {
        if (!(golem.level() instanceof ServerLevel level)) {
            return ItemStack.EMPTY;
        }
        BlockState state = level.getBlockState(blockPos);
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withParameter(LootContextParams.ORIGIN, mob.position());
        List<ItemStack> drops = state.getDrops(builder);
        return drops.stream().filter(GolemHarvestGoal::isCropDrop).findFirst()
                .orElse(drops.isEmpty() ? ItemStack.EMPTY : drops.getFirst());
    }

    private void resetBlock(Level level) {
        BlockState state = level.getBlockState(blockPos);
        for (Property<?> property : state.getProperties()) {
            if (property.getName().equalsIgnoreCase("age") && property instanceof IntegerProperty age) {
                level.playSound(null, blockPos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS);
                level.setBlockAndUpdate(blockPos, state.setValue(age, state.getBlock().defaultBlockState().getValue(age)));
                return;
            }
        }
        level.playSound(null, blockPos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS);
        level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
    }

    private static boolean isCropDrop(ItemStack stack) {
        return !(stack.getItem() instanceof ItemNameBlockItem) || stack.has(DataComponents.FOOD);
    }
}
