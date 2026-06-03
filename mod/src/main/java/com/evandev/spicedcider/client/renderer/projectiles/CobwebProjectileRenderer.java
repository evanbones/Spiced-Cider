package com.evandev.spicedcider.client.renderer.projectiles;

import com.evandev.spicedcider.client.models.projectile.CobwebProjectileModel;
import com.evandev.spicedcider.entities.projectiles.CobwebProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CobwebProjectileRenderer extends GeoEntityRenderer<CobwebProjectileEntity> {

    public CobwebProjectileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CobwebProjectileModel());
    }

    @Override
    public void render(CobwebProjectileEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        float scaleFactor = 1.0F;
        matrixStackIn.scale(scaleFactor, scaleFactor, scaleFactor);

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
}