package com.evandev.spicedcider.client.renderer.balloon;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.entities.balloon.BalloonEntity;
import com.evandev.spicedcider.registry.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;

public class BalloonRenderer extends EntityRenderer<BalloonEntity> {
    private static final Map<DyeColor, ResourceLocation> TEXTURES = new EnumMap<>(DyeColor.class);
    private static final float STRING_HALF_WIDTH = 1.0F / 32.0F;
    private static final float STRING_U0 = 11.0F / 32.0F;
    private static final float STRING_U1 = 12.0F / 32.0F;
    private static final float STRING_V0 = 16.0F / 32.0F;
    private static final float STRING_V1 = 1.0F;

    static {
        for (DyeColor color : DyeColor.values()) {
            TEXTURES.put(color, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/entity/balloon/" + color.getName() + ".png"));
        }
    }

    private final ModelPart balloon;

    public BalloonRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.balloon = context.bakeLayer(ModModelLayers.BALLOON).getChild("balloon");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("balloon",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                        .texOffs(16, 16).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void render(BalloonEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers, int packedLight) {
        Vec3 origin = entity.getPosition(partialTick);
        Vec3 knot = entity.knotPosition(partialTick);
        double time = entity.level().getGameTime() + partialTick;
        double phase = entity.phase();
        Vec3 anchor = entity.anchorPosition(partialTick);
        if (anchor == null) {
            anchor = knot.add(Math.sin(time * 0.1 + phase) * 0.15, -1.2, Math.cos(time * 0.1 + phase) * 0.15);
        }

        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
        poseStack.pushPose();
        poseStack.translate(knot.x - origin.x, knot.y - origin.y, knot.z - origin.z);
        renderString(poseStack, consumer, anchor.subtract(knot), packedLight);

        Vec3 up = knot.subtract(anchor).normalize();
        poseStack.mulPose(new Quaternionf().rotationTo(0.0F, 1.0F, 0.0F, (float) up.x, (float) up.y, (float) up.z));
        poseStack.mulPose(Axis.YP.rotation((float) (Math.sin(time * 0.02 + phase) * 0.6 + phase)));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.375F, 0.0F);
        this.balloon.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    private static void renderString(PoseStack poseStack, VertexConsumer consumer, Vec3 span, int light) {
        Vector3f end = new Vector3f((float) span.x, (float) span.y, (float) span.z);
        float length = end.length();
        if (length < 1.0E-4F) return;
        Vector3f dir = new Vector3f(end).div(length);
        Vector3f reference = Math.abs(dir.y) < 0.99F ? new Vector3f(0.0F, 1.0F, 0.0F) : new Vector3f(1.0F, 0.0F, 0.0F);
        Vector3f side = dir.cross(reference, new Vector3f()).normalize(STRING_HALF_WIDTH);
        Vector3f across = dir.cross(side, new Vector3f()).normalize(STRING_HALF_WIDTH);
        Vector3f[] corners = {
                new Vector3f(side).add(across),
                new Vector3f(side).sub(across),
                new Vector3f(side).negate().sub(across),
                new Vector3f(side).negate().add(across)
        };
        PoseStack.Pose pose = poseStack.last();
        for (int i = 0; i < 4; i++) {
            Vector3f a = corners[i];
            Vector3f b = corners[(i + 1) % 4];
            Vector3f normal = new Vector3f(a).add(b).normalize();
            vertex(consumer, pose, a, STRING_U0, STRING_V0, normal, light);
            vertex(consumer, pose, b, STRING_U1, STRING_V0, normal, light);
            vertex(consumer, pose, new Vector3f(b).add(end), STRING_U1, STRING_V1, normal, light);
            vertex(consumer, pose, new Vector3f(a).add(end), STRING_U0, STRING_V1, normal, light);
        }
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vector3f pos, float u, float v, Vector3f normal, int light) {
        consumer.addVertex(pose, pos.x, pos.y, pos.z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normal.x, normal.y, normal.z);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BalloonEntity entity) {
        return TEXTURES.get(entity.getColor());
    }
}
