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

public class SputterArrowfulModel<T extends SputterEntity> extends EntityModel<T> {
    private final ModelPart body;

    public SputterArrowfulModel(ModelPart root) {
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-5.5F, -2.0F, -5.5F, 11.0F, 2.0F, 11.0F), PartPose.offset(0.0F, 20.0F, 0.0F));
        addArrow(body, "arrow0", -0.1309F, 1.2654F);
        addArrow(body, "arrow1", -0.3054F, 1.8762F);
        addArrow(body, "arrow2", 0.3054F, 1.789F);
        addArrow(body, "arrow3", 0.2182F, 1.3963F);

        return LayerDefinition.create(mesh, 32, 32);
    }

    private static void addArrow(PartDefinition body, String name, float yRot, float zRot) {
        body.addOrReplaceChild(name, CubeListBuilder.create()
                .texOffs(0, 0).addBox(-16.0F, -2.5F, 0.0F, 16.0F, 5.0F, 0.0F)
                .texOffs(-5, 0).addBox(-16.0F, 0.0F, -2.5F, 16.0F, 0.0F, 5.0F)
                .texOffs(-5, 0).addBox(-15.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, yRot, zRot));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.body.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.body.xRot = headPitch * Mth.DEG_TO_RAD;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.body.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
