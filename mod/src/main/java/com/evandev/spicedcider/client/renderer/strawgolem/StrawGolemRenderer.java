package com.evandev.spicedcider.client.renderer.strawgolem;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.models.strawgolem.StrawGolemModel;
import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.evandev.spicedcider.registry.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StrawGolemRenderer extends MobRenderer<StrawGolem, StrawGolemModel> {
    private static final ResourceLocation[] TEXTURES = {
            texture("straw_golem"),
            texture("straw_golem_old"),
            texture("straw_golem_dying")
    };

    public StrawGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new StrawGolemModel(context.bakeLayer(ModModelLayers.STRAW_GOLEM)), 0.35F);
        this.addLayer(new StrawGolemBarrelLayer(this, context.getBlockRenderDispatcher()));
        this.addLayer(new StrawGolemItemLayer(this, context.getItemRenderer()));
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/entity/straw_golem/" + name + ".png");
    }

    @Override
    public void render(StrawGolem golem, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        if (golem.shouldShiver()) {
            poseStack.translate(golem.getRandom().nextDouble() * 0.02, 0.0, golem.getRandom().nextDouble() * 0.02);
        }
        super.render(golem, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(StrawGolem golem) {
        return TEXTURES[golem.healthStatus()];
    }
}
