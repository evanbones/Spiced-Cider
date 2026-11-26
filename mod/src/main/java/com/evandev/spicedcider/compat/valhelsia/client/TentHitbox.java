package com.evandev.spicedcider.compat.valhelsia.client;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stal111.valhelsia_structures.common.block.ExplorersTentBlock;
import com.stal111.valhelsia_structures.common.block.entity.ExplorersTentBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

public class TentHitbox {
    private static final double[][] SECTION = {
            {-0.967, 0.0}, {-1.446, 0.373}, {0.0, 2.097}, {1.446, 0.373}, {0.967, 0.0}
    };
    private static final double HALF_LENGTH = 1.1875;
    private static final double[][] PLANES = buildPlanes();

    private static double[][] buildPlanes() {
        double[][] planes = new double[SECTION.length + 2][];
        for (int i = 0; i < SECTION.length; i++) {
            double[] p = SECTION[i];
            double[] q = SECTION[(i + 1) % SECTION.length];
            double na = q[1] - p[1];
            double ny = p[0] - q[0];
            double len = Math.sqrt(na * na + ny * ny);
            na /= len;
            ny /= len;
            double d = na * p[0] + ny * p[1];
            if (d < ny * 0.8) {
                na = -na;
                ny = -ny;
                d = -d;
            }
            planes[i] = new double[]{na, ny, 0.0, d};
        }
        planes[SECTION.length] = new double[]{0.0, 0.0, 1.0, HALF_LENGTH};
        planes[SECTION.length + 1] = new double[]{0.0, 0.0, -1.0, HALF_LENGTH};
        return planes;
    }

    public static boolean enabled() {
        return SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.tentHitbox, false);
    }

    private static boolean ridgeAlongX(BlockState state) {
        return state.getValue(ExplorersTentBlock.FACING).getAxis() == Direction.Axis.X;
    }

    private static double[] toLocal(double x, double y, double z, boolean ridgeX) {
        return ridgeX ? new double[]{z, y, x} : new double[]{x, y, z};
    }

    private static Vec3 toWorld(double a, double y, double v, boolean ridgeX) {
        return ridgeX ? new Vec3(v, y, a) : new Vec3(a, y, v);
    }

    private static double[] clip(Vec3 from, Vec3 delta, BlockPos pos, boolean ridgeX) {
        double[] o = toLocal(from.x - pos.getX() - 0.5, from.y - pos.getY(), from.z - pos.getZ() - 0.5, ridgeX);
        double[] d = toLocal(delta.x, delta.y, delta.z, ridgeX);
        double enter = Double.NEGATIVE_INFINITY;
        double exit = Double.POSITIVE_INFINITY;
        int face = -1;
        for (int i = 0; i < PLANES.length; i++) {
            double[] n = PLANES[i];
            double dist = n[3] - (n[0] * o[0] + n[1] * o[1] + n[2] * o[2]);
            double den = n[0] * d[0] + n[1] * d[1] + n[2] * d[2];
            if (Math.abs(den) < 1.0E-9) {
                if (dist < 0) return null;
                continue;
            }
            double t = dist / den;
            if (den < 0) {
                if (t > enter) {
                    enter = t;
                    face = i;
                }
            } else if (t < exit) {
                exit = t;
            }
        }
        if (face < 0 || enter <= 0 || enter > exit || enter > 1) return null;
        return new double[]{enter, face};
    }

    public static HitResult pick(Entity entity, HitResult hit, double range, float partialTick) {
        if (!enabled()) return hit;
        Level level = entity.level();
        Vec3 eye = entity.getEyePosition(partialTick);
        Vec3 delta = entity.getViewVector(partialTick).scale(range);
        double best = hit.getType() == HitResult.Type.MISS ? 1.0 : Math.sqrt(hit.getLocation().distanceToSqr(eye)) / range;
        double reach = (entity instanceof Player player ? player.blockInteractionRange() : range) + 0.95;
        AABB area = new AABB(eye, eye.add(delta)).inflate(2.0);
        BlockHitResult result = null;
        int minX = SectionPos.blockToSectionCoord(Mth.floor(area.minX));
        int maxX = SectionPos.blockToSectionCoord(Mth.floor(area.maxX));
        int minZ = SectionPos.blockToSectionCoord(Mth.floor(area.minZ));
        int maxZ = SectionPos.blockToSectionCoord(Mth.floor(area.maxZ));
        for (int cx = minX; cx <= maxX; cx++) {
            for (int cz = minZ; cz <= maxZ; cz++) {
                if (!(level.getChunk(cx, cz, ChunkStatus.FULL, false) instanceof LevelChunk chunk)) continue;
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof ExplorersTentBlockEntity)) continue;
                    BlockPos pos = blockEntity.getBlockPos();
                    if (!area.contains(Vec3.atCenterOf(pos))) continue;
                    BlockState state = blockEntity.getBlockState();
                    if (!(state.getBlock() instanceof ExplorersTentBlock)) continue;
                    if (Math.sqrt(new AABB(pos).distanceToSqr(eye)) > reach) continue;
                    boolean ridgeX = ridgeAlongX(state);
                    double[] clip = clip(eye, delta, pos, ridgeX);
                    if (clip == null || clip[0] >= best) continue;
                    best = clip[0];
                    double[] n = PLANES[(int) clip[1]];
                    Vec3 normal = toWorld(n[0], n[1], n[2], ridgeX);
                    Vec3 at = eye.add(delta.scale(clip[0]));
                    Vec3 clamped = new Vec3(
                            Mth.clamp(at.x, pos.getX() - 0.499, pos.getX() + 1.499),
                            Mth.clamp(at.y, pos.getY() - 0.499, pos.getY() + 1.499),
                            Mth.clamp(at.z, pos.getZ() - 0.499, pos.getZ() + 1.499));
                    result = new BlockHitResult(clamped, Direction.getNearest(normal.x, normal.y, normal.z), pos, false);
                }
            }
        }
        return result != null ? result : hit;
    }

    public static boolean renderOutline(RenderHighlightEvent.Block event) {
        if (!enabled()) return false;
        BlockPos pos = event.getTarget().getBlockPos();
        Level level = event.getCamera().getEntity().level();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ExplorersTentBlock)) return false;
        boolean ridgeX = ridgeAlongX(state);
        Vec3 cam = event.getCamera().getPosition();
        double ox = pos.getX() + 0.5 - cam.x;
        double oy = pos.getY() - cam.y;
        double oz = pos.getZ() + 0.5 - cam.z;
        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
        PoseStack.Pose pose = event.getPoseStack().last();
        int count = SECTION.length;
        for (int i = 0; i < count; i++) {
            double[] p = SECTION[i];
            double[] q = SECTION[(i + 1) % count];
            line(consumer, pose, toWorld(p[0], p[1], -HALF_LENGTH, ridgeX), toWorld(q[0], q[1], -HALF_LENGTH, ridgeX), ox, oy, oz);
            line(consumer, pose, toWorld(p[0], p[1], HALF_LENGTH, ridgeX), toWorld(q[0], q[1], HALF_LENGTH, ridgeX), ox, oy, oz);
            line(consumer, pose, toWorld(p[0], p[1], -HALF_LENGTH, ridgeX), toWorld(p[0], p[1], HALF_LENGTH, ridgeX), ox, oy, oz);
        }
        return true;
    }

    private static void line(VertexConsumer consumer, PoseStack.Pose pose, Vec3 a, Vec3 b, double ox, double oy, double oz) {
        Vec3 n = b.subtract(a).normalize();
        consumer.addVertex(pose, (float) (a.x + ox), (float) (a.y + oy), (float) (a.z + oz)).setColor(0.0F, 0.0F, 0.0F, 0.4F).setNormal(pose, (float) n.x, (float) n.y, (float) n.z);
        consumer.addVertex(pose, (float) (b.x + ox), (float) (b.y + oy), (float) (b.z + oz)).setColor(0.0F, 0.0F, 0.0F, 0.4F).setNormal(pose, (float) n.x, (float) n.y, (float) n.z);
    }
}
