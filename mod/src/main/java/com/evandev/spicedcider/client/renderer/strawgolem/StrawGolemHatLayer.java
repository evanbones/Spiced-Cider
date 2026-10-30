package com.evandev.spicedcider.client.renderer.strawgolem;

import com.evandev.spicedcider.client.models.strawgolem.StrawGolemModel;
import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class StrawGolemHatLayer extends RenderLayer<StrawGolem, StrawGolemModel> {
    private static final float SCALE = 0.625F * 0.5F;
    private final ItemRenderer itemRenderer;

    public StrawGolemHatLayer(RenderLayerParent<StrawGolem, StrawGolemModel> parent, ItemRenderer itemRenderer) {
        super(parent);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, StrawGolem golem, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack hat = golem.getHat();
        if (hat.isEmpty() || golem.isInvisible()) return;
        poseStack.pushPose();
        getParentModel().translateToHead(poseStack);
        poseStack.translate(0.0F, -2.0F / 16.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(SCALE, -SCALE, -SCALE);
        itemRenderer.renderStatic(golem, hat, ItemDisplayContext.HEAD, false, poseStack, buffer, golem.level(), packedLight, LivingEntityRenderer.getOverlayCoords(golem, 0.0F), golem.getId());
        poseStack.popPose();
    }
}
