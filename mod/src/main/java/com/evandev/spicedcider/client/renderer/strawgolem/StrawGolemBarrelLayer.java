package com.evandev.spicedcider.client.renderer.strawgolem;

import com.evandev.spicedcider.client.models.strawgolem.StrawGolemModel;
import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class StrawGolemBarrelLayer extends RenderLayer<StrawGolem, StrawGolemModel> {
    private static final BlockState BARREL = Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, Direction.UP);
    private final BlockRenderDispatcher blockRenderer;

    public StrawGolemBarrelLayer(RenderLayerParent<StrawGolem, StrawGolemModel> parent, BlockRenderDispatcher blockRenderer) {
        super(parent);
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, StrawGolem golem, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!golem.hasBarrel() || golem.isInvisible()) return;
        poseStack.pushPose();
        getParentModel().translateToBody(poseStack);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.translate(-4.5F / 16.0F, -2.25F / 16.0F, -4.5F / 16.0F);
        poseStack.scale(8.75F / 16.0F, 10.5F / 16.0F, 8.75F / 16.0F);
        blockRenderer.renderSingleBlock(BARREL, poseStack, buffer, packedLight, LivingEntityRenderer.getOverlayCoords(golem, 0.0F));
        poseStack.popPose();
    }
}
