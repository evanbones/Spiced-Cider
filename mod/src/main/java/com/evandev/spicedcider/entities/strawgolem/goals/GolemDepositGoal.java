package com.evandev.spicedcider.entities.strawgolem.goals;

import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.evandev.spicedcider.entities.strawgolem.StrawGolemLids;
import com.evandev.spicedcider.entities.strawgolem.StrawGolemSenses;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GolemDepositGoal extends GolemMoveToBlockGoal {
    private static final int LID_OPEN_TICKS = 12;

    private boolean done;
    private boolean lidOpen;
    private int lidTicks;

    public GolemDepositGoal(StrawGolem golem) {
        super(golem);
    }

    @Override
    public void start() {
        done = false;
        lidOpen = false;
        lidTicks = 0;
        moveMobToBlock();
        tryTicks = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (lidTicks > 0) {
            golem.getLookControl().setLookAt(blockPos.getCenter().x, blockPos.getCenter().y, blockPos.getCenter().z);
            if (--lidTicks == 0) {
                done = true;
            }
        } else if (!golem.getMainHandItem().isEmpty() && StrawGolemSenses.canReach(mob, blockPos)) {
            golem.deliverer.deliver(blockPos);
            playSound("open");
            golem.getNavigation().stop();
            lidOpen = StrawGolemLids.open(golem.level(), blockPos);
            if (lidOpen) {
                lidTicks = LID_OPEN_TICKS;
            } else {
                done = true;
            }
        } else if (shouldRecalculatePath() && golemCollision()) {
            nudge();
        }
    }

    @Override
    public void stop() {
        if (lidOpen) {
            StrawGolemLids.close(golem.level(), blockPos);
            playSound("close");
            lidOpen = false;
        }
        lidTicks = 0;
        super.stop();
    }

    @Override
    public boolean canUse() {
        if (golem.getMainHandItem().isEmpty() || golem.pickupStatus() != 0) {
            return false;
        }
        if (!golem.deliverer.isUsable(blockPos) || golem.deliverer.shouldChangeDeliverable(blockPos)) {
            BlockPos target = golem.deliverer.getDeliverable();
            if (target == null) {
                return false;
            }
            blockPos = target;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (lidTicks > 0) {
            return StrawGolemSenses.isStorage(golem.level(), blockPos);
        }
        return !done && !golem.getMainHandItem().isEmpty() && StrawGolemSenses.isStorage(golem.level(), blockPos)
                && golem.getNavigation().getPath() != null;
    }

    private void playSound(String action) {
        SoundEvent sound = action.equals("open") ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE;
        BlockEntity blockEntity = golem.level().getBlockEntity(blockPos);
        if (blockEntity != null) {
            ResourceLocation type = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
            if (type != null) {
                SoundEvent typed = BuiltInRegistries.SOUND_EVENT.get(type.withPath("block." + type.getPath() + "." + action));
                if (typed != null) {
                    sound = typed;
                }
            }
        }
        golem.level().playSound(null, blockPos, sound, SoundSource.BLOCKS, 0.12F, golem.level().random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return golem.deliverer.isValid(pos);
    }
}
