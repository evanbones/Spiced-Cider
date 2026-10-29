package com.evandev.spicedcider.client.renderer.strawgolem;

import com.evandev.spicedcider.client.models.strawgolem.StrawGolemModel;
import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class StrawGolemItemLayer extends RenderLayer<StrawGolem, StrawGolemModel> {
    private final ItemRenderer itemRenderer;

    public StrawGolemItemLayer(RenderLayerParent<StrawGolem, StrawGolemModel> parent, ItemRenderer itemRenderer) {
        super(parent);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, StrawGolem golem, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = golem.getMainHandItem();
        if (stack.isEmpty()) return;
        poseStack.pushPose();
        getParentModel().translateToItem(poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        itemRenderer.renderStatic(golem, stack, ItemDisplayContext.FIXED, false, poseStack, buffer, golem.level(), packedLight, OverlayTexture.NO_OVERLAY, golem.getId());
        poseStack.popPose();
    }
}
