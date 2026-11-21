package com.evandev.spicedcider.client.stuckarrows;

import com.evandev.spicedcider.content.stuckarrows.StuckArrowSlots;
import com.evandev.spicedcider.content.stuckarrows.StuckArrows;
import com.evandev.spicedcider.mixin.minecraft.client.AgeableListModelAccessor;
import com.evandev.spicedcider.mixin.minecraft.client.ModelPartAccessor;
import com.evandev.spicedcider.registry.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

public final class StuckArrowPlacement {
    private static final float MODEL_PIVOT_Y = 1.501F;
    private static final float START_OUTSIDE = 0.6F;
    private static final float MAX_TRAVEL = 3.0F;
    private static final float FALLBACK_DEPTH = 0.15F;
    private static final float TIP_DEPTH = 0.3F;
    private static final float TIP_AHEAD = 0.2F;
    private static final float THICKNESS_SHARE = 0.7F;
    private static final float GRAZE = 0.5F;

    @FunctionalInterface
    public interface Drawer {
        void draw(PoseStack poseStack, MultiBufferSource buffer, int light, float x, float y, float z);
    }

    private record Part(ModelPart part, Matrix4f pose) {
    }

    private record Resolved(List<Float> hit, @Nullable ModelPart part, Vector3f point, Vector3f direction) {
    }

    private static final Map<LivingEntity, Map<EntityModel<?>, Map<Integer, Resolved>>> RESOLVED = new WeakHashMap<>();

    public static boolean render(RenderLayer<?, ?> layer, PoseStack poseStack, MultiBufferSource buffer, int light,
                                 LivingEntity entity, Drawer drawer) {
        if (!entity.hasData(ModAttachments.STUCK_ARROWS)) return false;
        StuckArrowSlots slots = entity.getData(ModAttachments.STUCK_ARROWS);
        if (slots.isEmpty()) return true;
        EntityModel<?> model = layer.getParentModel();
        List<Part> parts = parts(model);
        boolean worldSize = !StuckArrowsClient.isInventoryModel(entity);
        Map<Integer, Resolved> cache = RESOLVED.computeIfAbsent(entity, key -> new WeakHashMap<>()).computeIfAbsent(model, key -> new HashMap<>());
        for (StuckArrowSlots.Slot slot : slots.slots()) {
            Resolved resolved = cache.get(slot.index());
            if (resolved != null && Objects.equals(resolved.hit(), slot.hit())) {
                Part current = null;
                if (resolved.part() != null) {
                    for (Part part : parts) {
                        if (part.part == resolved.part()) {
                            current = part;
                            break;
                        }
                    }
                }
                if (resolved.part() == null || current != null) {
                    draw(poseStack, buffer, light, entity, slot, model, current, new Vector3f(resolved.point()), new Vector3f(resolved.direction()), worldSize, drawer);
                    continue;
                }
            }
            Vector3f origin = new Vector3f();
            Vector3f direction = new Vector3f();
            bodyRay(entity, slot, origin, direction);
            Vector3f modelOrigin = new Vector3f(-origin.x, -origin.y + MODEL_PIVOT_Y, origin.z);
            Vector3f modelDirection = new Vector3f(-direction.x, -direction.y, direction.z).normalize();
            modelOrigin.sub(new Vector3f(modelDirection).mul(START_OUTSIDE));

            Part best = null;
            float bestT = Float.MAX_VALUE;
            Vector3f bestPoint = null;
            Vector3f bestDirection = null;
            for (Part part : parts) {
                Matrix4f inverse = new Matrix4f(part.pose).invert();
                Vector3f localOrigin = inverse.transformPosition(new Vector3f(modelOrigin));
                Vector3f localDirection = inverse.transformDirection(new Vector3f(modelDirection));
                for (ModelPart.Cube cube : ((ModelPartAccessor) (Object) part.part).spicedcider$cubes()) {
                    float[] span = intersect(localOrigin, localDirection, cube);
                    if (span != null && span[0] < bestT && span[0] <= MAX_TRAVEL) {
                        bestT = span[0];
                        best = part;
                        Vector3f entry = new Vector3f(localDirection).mul(span[0]).add(localOrigin);
                        Vector3f normal = new Vector3f(localDirection).normalize();
                        if (span[2] >= 0) {
                            normal.zero().setComponent((int) span[2], Math.signum(localDirection.get((int) span[2])));
                        }
                        bestDirection = new Vector3f();
                        bestPoint = embed(part, inverse, cube, entry, modelDirection, normal, bestDirection);
                    }
                }
            }

            if (best == null) {
                float nearest = Float.MAX_VALUE;
                for (Part part : parts) {
                    Matrix4f inverse = new Matrix4f(part.pose).invert();
                    Vector3f localOrigin = inverse.transformPosition(new Vector3f(modelOrigin));
                    Vector3f localDirection = inverse.transformDirection(new Vector3f(modelDirection));
                    float length = Math.max(localDirection.length(), 1.0E-4F);
                    for (ModelPart.Cube cube : ((ModelPartAccessor) (Object) part.part).spicedcider$cubes()) {
                        Vector3f min = new Vector3f(cube.minX, cube.minY, cube.minZ).div(16.0F);
                        Vector3f max = new Vector3f(cube.maxX, cube.maxY, cube.maxZ).div(16.0F);
                        Vector3f center = new Vector3f(min).add(max).mul(0.5F);
                        float t = Math.max(0.0F, new Vector3f(center).sub(localOrigin).dot(localDirection) / (length * length));
                        Vector3f onRay = new Vector3f(localDirection).mul(t).add(localOrigin);
                        Vector3f onBox = new Vector3f(onRay).max(min).min(max);
                        float distance = onBox.distance(onRay);
                        if (distance < nearest) {
                            nearest = distance;
                            best = part;
                            Vector3f outside = new Vector3f(onRay).sub(onBox);
                            int axis = new Vector3f(outside).absolute().maxComponent();
                            Vector3f normal = new Vector3f().setComponent(axis, -Math.signum(outside.get(axis)));
                            if (normal.lengthSquared() < 0.5F) normal.set(localDirection).normalize();
                            bestDirection = new Vector3f();
                            bestPoint = embed(part, inverse, cube, onBox, modelDirection, normal, bestDirection);
                        }
                    }
                }
            }

            Vector3f point;
            if (best != null) {
                point = bestPoint;
            } else {
                bestDirection = modelDirection;
                point = new Vector3f(modelDirection).mul(START_OUTSIDE + FALLBACK_DEPTH).add(modelOrigin);
            }
            cache.put(slot.index(), new Resolved(slot.hit(), best == null ? null : best.part, new Vector3f(point), new Vector3f(bestDirection)));
            draw(poseStack, buffer, light, entity, slot, model, best, point, bestDirection, worldSize, drawer);
        }
        return true;
    }

    private static void draw(PoseStack poseStack, MultiBufferSource buffer, int light, LivingEntity entity, StuckArrowSlots.Slot slot,
                             EntityModel<?> model, @Nullable Part best, Vector3f point, Vector3f direction, boolean worldSize, Drawer drawer) {
        poseStack.pushPose();
        if (best != null) {
            poseStack.last().pose().mul(best.pose);
            poseStack.last().normal().mul(new Matrix3f(best.pose).normal());
        }
        poseStack.translate(point.x, point.y, point.z);
        if (worldSize) {
            float size = new Vector3f(1.0F, 0.0F, 0.0F).mulDirection(poseStack.last().pose()).length();
            if (size > 1.0E-4F) poseStack.scale(1.0F / size, 1.0F / size, 1.0F / size);
        }
        Vector3f arrow = direction.normalize();
        int part = best != null ? StuckArrowParts.classify(model, best.part) : StuckArrows.BODY;
        StuckArrowsClient.record(entity, slot.index(), part, poseStack.last(), arrow.x, arrow.y, arrow.z, light,
                (pose, source, packedLight) -> drawer.draw(pose, source, packedLight, arrow.x, arrow.y, arrow.z));
        drawer.draw(poseStack, buffer, light, arrow.x, arrow.y, arrow.z);
        poseStack.popPose();
    }

    private static void bodyRay(LivingEntity entity, StuckArrowSlots.Slot slot, Vector3f origin, Vector3f direction) {
        if (slot.hasHit()) {
            List<Float> hit = slot.hit();
            origin.set(hit.get(0), hit.get(1), hit.get(2));
            direction.set(hit.get(3), hit.get(4), hit.get(5));
            return;
        }
        float scale = Math.max(entity.getScale(), 0.01F);
        float yaw = (slot.index() * 137.5F + entity.getId() * 31.0F) * Mth.DEG_TO_RAD;
        float pitch = ((slot.index() * 53 + entity.getId()) % 60 - 30) * Mth.DEG_TO_RAD;
        direction.set(Mth.sin(yaw) * Mth.cos(pitch), Mth.sin(pitch), Mth.cos(yaw) * Mth.cos(pitch)).normalize();
        float height = entity.getBbHeight() / scale;
        float width = entity.getBbWidth() / scale;
        origin.set(0.0F, height * 0.55F, 0.0F).sub(new Vector3f(direction).mul(width));
    }

    private static Vector3f embed(Part part, Matrix4f inverse, ModelPart.Cube cube, Vector3f entry, Vector3f modelDirection,
                                  Vector3f localNormal, Vector3f direction) {
        Vector3f modelNormal = part.pose.transformDirection(new Vector3f(localNormal));
        if (modelNormal.lengthSquared() > 1.0E-8F) modelNormal.normalize();
        Vector3f modelArrow = new Vector3f(modelNormal).mul(GRAZE).add(modelDirection).normalize();
        inverse.transformDirection(modelArrow, direction);
        float[] span = intersect(new Vector3f(direction).mul(-1.0E-3F).add(entry), direction, cube);
        float thickness = span != null ? Math.max(span[1] - 1.0E-3F, 0.0F) : 0.0F;
        float tip = Math.min(TIP_DEPTH, thickness * THICKNESS_SHARE);
        return new Vector3f(direction).mul(tip - TIP_AHEAD).add(entry);
    }

    @Nullable
    private static float[] intersect(Vector3f origin, Vector3f direction, ModelPart.Cube cube) {
        float[] min = {cube.minX / 16.0F, cube.minY / 16.0F, cube.minZ / 16.0F};
        float[] max = {cube.maxX / 16.0F, cube.maxY / 16.0F, cube.maxZ / 16.0F};
        float[] o = {origin.x, origin.y, origin.z};
        float[] d = {direction.x, direction.y, direction.z};
        float near = -Float.MAX_VALUE;
        float far = Float.MAX_VALUE;
        int enter = -1;
        for (int axis = 0; axis < 3; axis++) {
            if (Math.abs(d[axis]) < 1.0E-7F) {
                if (o[axis] < min[axis] || o[axis] > max[axis]) return null;
                continue;
            }
            float t1 = (min[axis] - o[axis]) / d[axis];
            float t2 = (max[axis] - o[axis]) / d[axis];
            float axisNear = Math.min(t1, t2);
            if (axisNear > near) {
                near = axisNear;
                enter = axis;
            }
            far = Math.min(far, Math.max(t1, t2));
        }
        if (near > far || far < 0.0F) return null;
        return new float[]{Math.max(near, 0.0F), far, near >= 0.0F ? enter : -1};
    }

    private static List<Part> parts(EntityModel<?> model) {
        List<Part> parts = new ArrayList<>();
        List<ModelPart> roots = roots(model);
        if (roots == null) return parts;
        for (ModelPart root : roots) {
            collect(root, new PoseStack(), parts);
        }
        return parts;
    }

    @Nullable
    private static List<ModelPart> roots(EntityModel<?> model) {
        if (model instanceof HierarchicalModel<?> hierarchical) return List.of(hierarchical.root());
        if (model instanceof AgeableListModel<?> ageable) {
            List<ModelPart> roots = new ArrayList<>();
            ((AgeableListModelAccessor) ageable).spicedcider$headParts().forEach(roots::add);
            ((AgeableListModelAccessor) ageable).spicedcider$bodyParts().forEach(roots::add);
            return roots;
        }
        return null;
    }

    private static void collect(ModelPart part, PoseStack poseStack, List<Part> parts) {
        if (!part.visible) return;
        poseStack.pushPose();
        part.translateAndRotate(poseStack);
        if (!part.skipDraw && !((ModelPartAccessor) (Object) part).spicedcider$cubes().isEmpty()) {
            parts.add(new Part(part, new Matrix4f(poseStack.last().pose())));
        }
        for (ModelPart child : ((ModelPartAccessor) (Object) part).spicedcider$children().values()) {
            collect(child, poseStack, parts);
        }
        poseStack.popPose();
    }
}
