package com.evandev.spicedcider.entities.strawgolem.goals;

import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public abstract class GolemMoveToBlockGoal extends MoveToBlockGoal {
    protected final StrawGolem golem;

    public GolemMoveToBlockGoal(StrawGolem golem) {
        super(golem, StrawGolem.WALK_SPEED, StrawGolem.SEARCH_RANGE, StrawGolem.SEARCH_RANGE_VERTICAL);
        this.golem = golem;
    }

    protected boolean golemCollision() {
        return !golem.level().getEntitiesOfClass(StrawGolem.class, golem.getBoundingBox().inflate(0.3),
                other -> other != golem && !other.position().equals(golem.position())).isEmpty();
    }

    protected void nudge() {
        Direction side = golem.getDirection().getCounterClockWise();
        golem.push(Vec3.atLowerCornerOf(side.getNormal()).scale(0.05));
    }

    @Override
    protected void moveMobToBlock() {
        mob.getNavigation().moveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, speedModifier);
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return false;
    }
}
