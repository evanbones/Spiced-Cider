package com.evandev.spicedcider.entities.strawgolem.goals;

import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GolemWanderGoal extends WaterAvoidingRandomStrollGoal {
    private final StrawGolem golem;
    private BlockPos startPos = BlockPos.ZERO;

    public GolemWanderGoal(StrawGolem golem) {
        super(golem, StrawGolem.WALK_SPEED);
        this.golem = golem;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        BlockPos home = golem.getPriorityPos();
        if (home == null) {
            return super.getPosition();
        }
        Vec3 anchor = Vec3.atCenterOf(home);
        double tether = StrawGolem.TETHER_RANGE;
        if (mob.position().distanceToSqr(anchor) > tether * tether) {
            return LandRandomPos.getPosTowards(mob, (int) tether, 7, anchor);
        }
        for (int i = 0; i < 8; i++) {
            Vec3 pos = super.getPosition();
            if (pos != null && pos.distanceToSqr(anchor) <= tether * tether) {
                return pos;
            }
        }
        return null;
    }

    @Override
    public void start() {
        super.start();
        startPos = mob.blockPosition();
    }

    @Override
    public boolean canUse() {
        return mob.getMainHandItem().isEmpty() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && startPos.distManhattan(mob.blockPosition()) < StrawGolem.WANDER_RANGE;
    }
}
