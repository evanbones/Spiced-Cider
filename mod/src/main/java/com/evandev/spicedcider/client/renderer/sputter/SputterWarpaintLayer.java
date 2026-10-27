package com.evandev.spicedcider.client.renderer.sputter;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.models.sputter.SputterModel;
import com.evandev.spicedcider.entities.sputter.SputterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class SputterWarpaintLayer extends RenderLayer<SputterEntity, SputterModel<SputterEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/entity/sputter/sputter_warpaint.png");

    public SputterWarpaintLayer(RenderLayerParent<SputterEntity, SputterModel<SputterEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SputterEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        DyeColor color = entity.getWarpaintColor();
        if (color != null && !entity.isInvisible()) {
            renderColoredCutoutModel(this.getParentModel(), TEXTURE, poseStack, buffer, packedLight, entity, color.getTextureDiffuseColor());
        }
    }
}
