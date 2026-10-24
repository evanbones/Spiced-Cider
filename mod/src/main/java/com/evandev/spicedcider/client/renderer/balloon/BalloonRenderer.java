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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.EnumMap;
import java.util.Map;

public class BalloonRenderer extends EntityRenderer<BalloonEntity> {
    private static final Map<DyeColor, ResourceLocation> TEXTURES = new EnumMap<>(DyeColor.class);
    private static final float STRING_WIDTH = 0.025F;
    private static final int STRING_SEGMENTS = 24;
    private static final float STRING_RED = 0.85F;
    private static final float STRING_GREEN = 0.85F;
    private static final float STRING_BLUE = 0.82F;

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

        poseStack.pushPose();
        poseStack.translate(knot.x - origin.x, knot.y - origin.y, knot.z - origin.z);
        renderString(poseStack, buffers, entity.level(), knot, anchor);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));

        Vec3 up = knot.subtract(anchor).normalize();
        poseStack.mulPose(new Quaternionf().rotationTo(0.0F, 1.0F, 0.0F, (float) up.x, (float) up.y, (float) up.z));
        poseStack.mulPose(Axis.YP.rotation((float) (Math.sin(time * 0.02 + phase) * 0.6 + phase)));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.375F, 0.0F);
        this.balloon.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    private static void renderString(PoseStack poseStack, MultiBufferSource buffers, Level level, Vec3 knot, Vec3 anchor) {
        float dx = (float) (anchor.x - knot.x);
        float dy = (float) (anchor.y - knot.y);
        float dz = (float) (anchor.z - knot.z);
        float horizontal = Mth.sqrt(dx * dx + dz * dz);
        float sideX = horizontal < 1.0E-4F ? 0.0F : dz / horizontal * STRING_WIDTH / 2.0F;
        float sideZ = horizontal < 1.0E-4F ? STRING_WIDTH / 2.0F : dx / horizontal * STRING_WIDTH / 2.0F;
        BlockPos knotPos = BlockPos.containing(knot);
        BlockPos anchorPos = BlockPos.containing(anchor);
        int knotBlock = level.getBrightness(LightLayer.BLOCK, knotPos);
        int anchorBlock = level.getBrightness(LightLayer.BLOCK, anchorPos);
        int knotSky = level.getBrightness(LightLayer.SKY, knotPos);
        int anchorSky = level.getBrightness(LightLayer.SKY, anchorPos);
        VertexConsumer consumer = buffers.getBuffer(RenderType.leash());
        Matrix4f pose = poseStack.last().pose();
        for (int i = 0; i <= STRING_SEGMENTS; i++) {
            addVertexPair(consumer, pose, dx, dy, dz, knotBlock, anchorBlock, knotSky, anchorSky, STRING_WIDTH, STRING_WIDTH, sideX, sideZ, i, false);
        }
        for (int i = STRING_SEGMENTS; i >= 0; i--) {
            addVertexPair(consumer, pose, dx, dy, dz, knotBlock, anchorBlock, knotSky, anchorSky, STRING_WIDTH, 0.0F, sideX, sideZ, i, true);
        }
    }

    private static void addVertexPair(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, int knotBlock, int anchorBlock,
                                      int knotSky, int anchorSky, float yOffset, float dy, float sideX, float sideZ, int index, boolean reverse) {
        float t = (float) index / STRING_SEGMENTS;
        int light = LightTexture.pack((int) Mth.lerp(t, knotBlock, anchorBlock), (int) Mth.lerp(t, knotSky, anchorSky));
        float shade = index % 2 == (reverse ? 1 : 0) ? 0.7F : 1.0F;
        float r = STRING_RED * shade;
        float g = STRING_GREEN * shade;
        float b = STRING_BLUE * shade;
        float px = x * t;
        float py = y > 0.0F ? y * t * t : y - y * (1.0F - t) * (1.0F - t);
        float pz = z * t;
        consumer.addVertex(pose, px - sideX, py + dy, pz + sideZ).setColor(r, g, b, 1.0F).setLight(light);
        consumer.addVertex(pose, px + sideX, py + yOffset - dy, pz - sideZ).setColor(r, g, b, 1.0F).setLight(light);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BalloonEntity entity) {
        return TEXTURES.get(entity.getColor());
    }
}
