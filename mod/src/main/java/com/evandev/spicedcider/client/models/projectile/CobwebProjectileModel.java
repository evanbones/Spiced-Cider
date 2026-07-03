package com.evandev.spicedcider.client.models.projectile;

import com.evandev.spicedcider.entities.projectiles.CobwebProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CobwebProjectileModel extends EntityModel<CobwebProjectileEntity> {

    private final ModelPart everything;

    public CobwebProjectileModel(ModelPart root) {
        this.everything = root.getChild("everything");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "everything",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -0.5F, 0.0F, -1.5708F, 0.0F)
        );

        return LayerDefinition.create(mesh, 32, 16);
    }

    @Override
    public void setupAnim(@NotNull CobwebProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float t = (ageInTicks % 6.6667F) / 6.6667F;
        float pulse = 0.1F * Mth.sin(t * (float) Math.PI);

        everything.xScale = 1.0F - pulse;
        everything.yScale = 1.0F - pulse;
        everything.zScale = 1.0F;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        everything.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
