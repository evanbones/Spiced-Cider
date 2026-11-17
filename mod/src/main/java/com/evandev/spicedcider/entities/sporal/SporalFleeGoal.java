package com.evandev.spicedcider.entities.sporal;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SporalFleeGoal extends AvoidEntityGoal<Player> {
    private static final double PANIC_DISTANCE = 6.0;

    private final Sporal sporal;

    public SporalFleeGoal(Sporal sporal, float maxDistance, double walkSpeedModifier, double sprintSpeedModifier) {
        super(sporal, Player.class, maxDistance, walkSpeedModifier, sprintSpeedModifier, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
        this.sporal = sporal;
    }

    @Override
    public boolean canUse() {
        return !this.sporal.isSupporting() && super.canUse();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.toAvoid == null || !this.sporal.canDash()) return;

        if (this.sporal.distanceToSqr(this.toAvoid) < PANIC_DISTANCE * PANIC_DISTANCE) {
            if (this.path != null && this.path.getTarget() != null) {
                this.sporal.dashTowards(Vec3.atBottomCenterOf(this.path.getTarget()));
            } else {
                this.sporal.dashAwayFrom(this.toAvoid.position());
            }
        }
    }
}
