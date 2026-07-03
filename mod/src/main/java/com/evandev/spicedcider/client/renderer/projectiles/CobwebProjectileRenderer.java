package com.evandev.spicedcider.client.renderer.projectiles;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.models.projectile.CobwebProjectileModel;
import com.evandev.spicedcider.entities.projectiles.CobwebProjectileEntity;
import com.evandev.spicedcider.registry.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CobwebProjectileRenderer extends EntityRenderer<CobwebProjectileEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/entity/projectile/web_projectile.png");

    private final CobwebProjectileModel model;

    public CobwebProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CobwebProjectileModel(context.bakeLayer(ModModelLayers.COBWEB_PROJECTILE));
    }

    @Override
    public void render(CobwebProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getViewXRot(partialTicks)));

        model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTicks, 0.0F, 0.0F);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CobwebProjectileEntity entity) {
        return TEXTURE;
    }
}
