package com.evandev.spicedcider.entities.strawgolem.goals;

import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
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
    private boolean done;

    public GolemDepositGoal(StrawGolem golem) {
        super(golem);
    }

    @Override
    public void start() {
        done = false;
        moveMobToBlock();
        tryTicks = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!golem.getMainHandItem().isEmpty() && StrawGolemSenses.canReach(mob, blockPos)) {
            golem.deliverer.deliver(blockPos);
            playOpenSound();
            golem.getNavigation().stop();
            done = true;
        } else if (shouldRecalculatePath() && golemCollision()) {
            nudge();
        }
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
        return !done && !golem.getMainHandItem().isEmpty() && StrawGolemSenses.isStorage(golem.level(), blockPos)
                && golem.getNavigation().getPath() != null;
    }

    private void playOpenSound() {
        SoundEvent sound = SoundEvents.CHEST_OPEN;
        BlockEntity blockEntity = golem.level().getBlockEntity(blockPos);
        if (blockEntity != null) {
            ResourceLocation type = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
            if (type != null) {
                SoundEvent typed = BuiltInRegistries.SOUND_EVENT.get(type.withPath("block." + type.getPath() + ".open"));
                if (typed != null) {
                    sound = typed;
                }
            }
        }
        golem.level().playSound(null, blockPos, sound, SoundSource.BLOCKS, 0.5F, golem.level().random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return golem.deliverer.isValid(pos);
    }
}
