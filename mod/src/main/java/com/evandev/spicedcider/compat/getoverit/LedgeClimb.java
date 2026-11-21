package com.evandev.spicedcider.compat.getoverit;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import kobber.getoverit.PlayerWithClimbingAnim;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LedgeClimb {
    private static final double MIN_LEDGE = 1.3;
    private static final double MAX_LEDGE = 2.25;
    private static final double MAX_ABOVE_FEET = 1.9;
    private static final double WALL_PROBE = 0.1;
    private static final double LEDGE_PROBE = 0.3;
    private static final double RISE_ACCEL = 0.2;
    private static final double RISE_MAX = 0.45;
    private static final double HUG = 0.05;
    private static final double TOP_PUSH = 0.25;
    private static final double TOP_LIFT = 0.1;
    private static final int MAX_TICKS = 20;

    private static boolean climbing;
    private static boolean inTravel;
    private static double ledgeTop;
    private static Vec3 facing = Vec3.ZERO;
    private static int ticks;
    private static double lastGroundY = Double.NaN;

    public static boolean enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.ledgeClimbRework, false);
    }

    public static boolean isClimbing(Player player) {
        return climbing && !inTravel && player.isLocalPlayer();
    }

    public static void travel(Player player, Vec3 input, Runnable original) {
        if (player.onGround()) lastGroundY = player.getY();
        if (climbing) {
            tickClimb(player, input);
        } else if (canStart(player, input)) {
            start(player);
            tickClimb(player, input);
        }
        inTravel = true;
        try {
            original.run();
        } finally {
            inTravel = false;
        }
    }

    private static boolean canStart(Player player, Vec3 input) {
        if (!canMove(player) || player.onGround() || player.onClimbable() || player.isPassenger()
                || player.isSwimming() || player.isFallFlying()) {
            return false;
        }
        if (!player.horizontalCollision || input.z <= 0.01 || Double.isNaN(lastGroundY)) return false;

        Level level = player.level();
        AABB box = player.getBoundingBox();
        Vec3 dir = facing(player);
        AABB front = box.move(dir.scale(WALL_PROBE));
        if (level.noCollision(player, front)) return false;

        double top = wallTop(level, player, front.setMaxY(box.minY + MAX_ABOVE_FEET + 0.5), box.minY);
        if (Double.isNaN(top)) return false;
        double ledge = top - lastGroundY;
        if (ledge < MIN_LEDGE || ledge > MAX_LEDGE || top - box.minY > MAX_ABOVE_FEET) return false;

        double rise = top + 0.01 - box.minY;
        if (!level.noCollision(player, box.expandTowards(0, rise, 0))) return false;
        if (!level.noCollision(player, box.move(0, rise, 0).move(dir.scale(LEDGE_PROBE)))) return false;
        if (ModList.get().isLoaded("modest_meals") && !LedgeClimbStamina.canClimb(player)) return false;

        ledgeTop = top;
        facing = dir;
        return true;
    }

    private static void start(Player player) {
        climbing = true;
        ticks = 0;
        if (player instanceof PlayerWithClimbingAnim anim) {
            anim.getoverit$setClimbAnimTime(anim.getoverit$getMaxClimbAnimTime());
        }
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.ledgeClimbStamina, false)) {
            LedgeClimbNet.sendClimb();
        }
    }

    private static void tickClimb(Player player, Vec3 input) {
        if (!canMove(player) || player.onGround() || input.z <= 0.01 || ++ticks > MAX_TICKS) {
            climbing = false;
            return;
        }
        Vec3 motion = player.getDeltaMovement();
        if (player.getY() >= ledgeTop) {
            player.setDeltaMovement(facing.x * TOP_PUSH, Math.min(motion.y, TOP_LIFT), facing.z * TOP_PUSH);
            climbing = false;
            return;
        }
        double up = Math.min(Math.max(motion.y, 0.0) + RISE_ACCEL, RISE_MAX);
        player.setDeltaMovement(facing.x * HUG, up, facing.z * HUG);
        player.resetFallDistance();
    }

    private static boolean canMove(Player player) {
        return !player.isCrouching() && !player.isSpectator() && !player.getAbilities().flying;
    }

    private static Vec3 facing(Player player) {
        float yaw = player.getYRot() * ((float) Math.PI / 180.0F);
        return new Vec3(-Math.sin(yaw), 0.0, Math.cos(yaw));
    }

    private static double wallTop(Level level, Player player, AABB column, double feet) {
        List<VoxelShape> shapes = new ArrayList<>();
        for (VoxelShape shape : level.getBlockCollisions(player, column)) {
            if (!shape.isEmpty()) shapes.add(shape);
        }
        if (shapes.isEmpty()) return Double.NaN;
        shapes.sort(Comparator.comparingDouble(shape -> shape.min(Direction.Axis.Y)));
        double top = feet;
        boolean touched = false;
        for (VoxelShape shape : shapes) {
            if (shape.min(Direction.Axis.Y) > top + 1.0E-4) break;
            top = Math.max(top, shape.max(Direction.Axis.Y));
            touched = true;
        }
        return touched ? top : Double.NaN;
    }
}
