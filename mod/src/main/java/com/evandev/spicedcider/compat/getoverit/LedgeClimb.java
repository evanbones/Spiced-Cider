package com.evandev.spicedcider.compat.getoverit;

import com.evandev.spicedcider.config.SpicedCiderConfig;
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
    private static final double MAX_LEDGE = 2.6;
    private static final double WALL_PROBE = 0.1;
    private static final double COLUMN_HEIGHT = 2.8;
    private static final int JUMP_BUFFER_TICKS = 8;

    private static double lastGroundY = Double.NaN;
    private static int ticksSinceJump = Integer.MAX_VALUE;
    private static boolean jumpHeld;

    public static boolean reworked() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.ledgeClimbRework, false);
    }

    public static void tick(Player player, boolean jumping) {
        if (player.onGround()) lastGroundY = player.getY();
        if (jumping && !jumpHeld) ticksSinceJump = 0;
        else if (ticksSinceJump < Integer.MAX_VALUE) ticksSinceJump++;
        jumpHeld = jumping;
    }

    public static boolean allowStart(Player player) {
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.ledgeClimbing, true)) return false;
        if (ModList.get().isLoaded("modest_meals") && !LedgeClimbStamina.canClimb(player)) return false;
        if (!reworked()) return true;
        if (!jumpHeld && ticksSinceJump > JUMP_BUFFER_TICKS) return false;
        if (Double.isNaN(lastGroundY)) return false;
        Level level = player.level();
        AABB box = player.getBoundingBox();
        AABB front = box.move(facing(player).scale(WALL_PROBE));
        double top = wallTop(level, player, front.setMaxY(box.minY + COLUMN_HEIGHT), box.minY);
        if (Double.isNaN(top)) return false;
        double ledge = top - lastGroundY;
        return ledge >= MIN_LEDGE && ledge <= MAX_LEDGE;
    }

    public static void started() {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.ledgeClimbStamina, false)) {
            LedgeClimbNet.sendClimb();
        }
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
