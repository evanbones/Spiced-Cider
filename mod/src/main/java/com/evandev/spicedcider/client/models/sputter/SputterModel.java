package com.evandev.spicedcider.client.models.sputter;

import com.evandev.spicedcider.entities.sputter.SputterEntity;
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

public class SputterModel<T extends SputterEntity> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public SputterModel(ModelPart root) {
        this.body = root.getChild("body");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 11).addBox(-5.5F, -2.0F, -5.5F, 11.0F, 2.0F, 11.0F)
                .texOffs(46, 4).addBox(-5.5F, -11.0F, -3.5F, 2.0F, 9.0F, 7.0F)
                .texOffs(0, 0).addBox(-5.5F, -11.0F, -5.5F, 11.0F, 9.0F, 2.0F)
                .texOffs(46, 4).addBox(3.5F, -11.0F, -3.5F, 2.0F, 9.0F, 7.0F)
                .texOffs(26, 0).addBox(-5.5F, -11.0F, 3.5F, 11.0F, 9.0F, 2.0F), PartPose.offset(0.0F, 20.0F, 0.0F));
        body.addOrReplaceChild("snout", CubeListBuilder.create()
                .texOffs(0, 24).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 5.0F), PartPose.offset(0.0F, -5.5F, -8.5F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(44, 23).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 5.0F, 4.0F), PartPose.offset(3.0F, 20.0F, -0.5F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(44, 23).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 5.0F, 4.0F), PartPose.offset(-3.0F, 20.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.body.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.body.xRot = headPitch * Mth.DEG_TO_RAD;
        this.rightLeg.xRot = -1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.leftLeg.xRot = 1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.body.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.leftLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.rightLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
