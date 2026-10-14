package com.evandev.spicedcider.client.devbridge;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public final class Aim {
    private volatile Supplier<Vec3> lock;

    public sealed interface Target permits BlockTarget, EntityTarget, PointTarget {
        Vec3 point();
    }

    public record BlockTarget(BlockPos pos, Direction face) implements Target {
        public Vec3 point() {
            return Vec3.atCenterOf(pos).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
        }
    }

    public record EntityTarget(Entity entity) implements Target {
        public Vec3 point() {
            return entity.getBoundingBox().getCenter();
        }
    }

    public record PointTarget(Vec3 point) implements Target {
    }

    public static Target parse(Map<String, String> query) {
        Minecraft mc = Minecraft.getInstance();
        if (query.containsKey("block")) {
            String[] parts = query.get("block").split(",");
            BlockPos pos = new BlockPos(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()));
            Direction face = Direction.byName(query.getOrDefault("face", "up"));
            if (face == null) throw new IllegalArgumentException("Unknown face " + query.get("face"));
            return new BlockTarget(pos, face);
        }
        if (query.containsKey("entity")) {
            String id = query.get("entity");
            LocalPlayer player = mc.player;
            Iterable<Entity> entities = mc.level.entitiesForRendering();
            Entity found;
            if (id.contains("-") && id.length() == 36) {
                UUID uuid = UUID.fromString(id);
                found = StreamSupport.stream(entities.spliterator(), false).filter(e -> e.getUUID().equals(uuid)).findFirst().orElse(null);
            } else {
                ResourceLocation type = ResourceLocation.parse(id);
                found = StreamSupport.stream(entities.spliterator(), false)
                        .filter(e -> e != player && net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).equals(type))
                        .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                        .orElse(null);
            }
            if (found == null) throw new IllegalArgumentException("No entity matching " + id);
            return new EntityTarget(found);
        }
        if (query.containsKey("pos")) {
            String[] parts = query.get("pos").split(",");
            return new PointTarget(new Vec3(Double.parseDouble(parts[0].trim()), Double.parseDouble(parts[1].trim()), Double.parseDouble(parts[2].trim())));
        }
        throw new IllegalArgumentException("Give block=x,y,z[&face=], entity=<uuid|type id> or pos=x,y,z");
    }

    public static void face(LocalPlayer player, Vec3 point) {
        Vec3 eye = player.getEyePosition();
        double dx = point.x - eye.x;
        double dy = point.y - eye.y;
        double dz = point.z - eye.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) (-(Mth.atan2(dy, horizontal) * Mth.RAD_TO_DEG));
        player.setYRot(yaw);
        player.setXRot(Mth.clamp(pitch, -90.0F, 90.0F));
        player.setYHeadRot(yaw);
        player.yRotO = yaw;
        player.xRotO = player.getXRot();
    }

    public JsonObject lookAt(Target target, boolean keep) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) throw new IllegalStateException("Not in a world");
        face(player, target.point());
        lock = keep ? target::point : null;
        return describe(player);
    }

    public void clear() {
        lock = null;
    }

    public void onClientTick() {
        Supplier<Vec3> current = lock;
        LocalPlayer player = Minecraft.getInstance().player;
        if (current == null || player == null) return;
        face(player, current.get());
    }

    public static String interact(Target target, String action, InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.gameMode == null) throw new IllegalStateException("Not in a world");
        boolean attack = "attack".equals(action);
        if (target == null) {
            if (attack) throw new IllegalArgumentException("attack needs a block or entity");
            return mc.gameMode.useItem(player, hand).toString();
        }
        face(player, target.point());
        if (target instanceof EntityTarget(Entity entity)) {
            if (attack) {
                mc.gameMode.attack(player, entity);
                player.swing(hand);
                return "ATTACKED";
            }
            InteractionResult result = mc.gameMode.interactAt(player, entity, new EntityHitResult(entity, target.point()), hand);
            if (!result.consumesAction()) result = mc.gameMode.interact(player, entity, hand);
            if (!result.consumesAction()) result = mc.gameMode.useItem(player, hand);
            if (result.shouldSwing()) player.swing(hand);
            return result.toString();
        }
        if (target instanceof BlockTarget(BlockPos pos, Direction face)) {
            if (attack) {
                mc.gameMode.startDestroyBlock(pos, face);
                player.swing(hand);
                return "ATTACKED";
            }
            InteractionResult result = mc.gameMode.useItemOn(player, hand, new BlockHitResult(target.point(), face, pos, false));
            if (!result.consumesAction()) result = mc.gameMode.useItem(player, hand);
            if (result.shouldSwing()) player.swing(hand);
            return result.toString();
        }
        InteractionResult result = mc.gameMode.useItem(player, hand);
        if (result.shouldSwing()) player.swing(hand);
        return result.toString();
    }

    private JsonObject describe(LocalPlayer player) {
        JsonObject out = new JsonObject();
        out.addProperty("ok", true);
        out.addProperty("yaw", player.getYRot());
        out.addProperty("pitch", player.getXRot());
        out.addProperty("locked", lock != null);
        return out;
    }
}
