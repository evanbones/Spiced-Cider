package com.evandev.spicedcider.client.models.strawgolem;

import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3f;

public class StrawGolemModel extends HierarchicalModel<StrawGolem> {
    private static final Vector3f CACHE = new Vector3f();
    private static final float MOVING_THRESHOLD = 0.015F;
    private final ModelPart root;
    private final ModelPart upper;
    private final ModelPart body;
    private final ModelPart hat;
    private final ModelPart item;

    public StrawGolemModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
        this.upper = root.getChild("upper");
        this.body = upper.getChild("body");
        this.hat = upper.getChild("head").getChild("hat");
        this.item = root.getChild("item");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition lower = root.addOrReplaceChild("lower", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));
        lower.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(8, 20).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(2.0F, 1.0F, 0.0F));
        lower.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(8, 20).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(-2.0F, 1.0F, 0.0F));

        PartDefinition upper = root.addOrReplaceChild("upper", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));
        PartDefinition body = upper.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.ZERO);
        PartDefinition arms = body.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 0.0F));
        arms.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(0, 20).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.offset(-5.0F, 0.0F, 0.0F));
        arms.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(0, 20).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.offset(5.0F, 0.0F, 0.0F));

        PartDefinition head = upper.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(32, 0).addBox(-2.0F, -3.9F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(0.0F, -8.0F, 0.0F));
        head.addOrReplaceChild("hat", CubeListBuilder.create()
                .texOffs(0, 32).addBox(-5.5F, -2.5F, -5.5F, 11.0F, 0.0F, 11.0F)
                .texOffs(16, 20).addBox(-2.5F, -4.55F, -2.5F, 5.0F, 2.0F, 5.0F), PartPose.ZERO);

        root.addOrReplaceChild("item", CubeListBuilder.create(), PartPose.offset(0.0F, 15.5F, -5.5F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(StrawGolem golem, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        root.getAllParts().forEach(ModelPart::resetPose);
        hat.visible = golem.hasHat();

        if (golem.harvestAnimationState.isStarted()) {
            animate(golem.harvestAnimationState, golem.isHarvestingBlock() ? StrawGolemAnimations.HARVEST_BLOCK : StrawGolemAnimations.HARVEST_ITEM, ageInTicks);
            return;
        }

        boolean moving = limbSwingAmount > MOVING_THRESHOLD;
        boolean running = golem.isPanicking();
        long idleTime = (long) (ageInTicks * 50.0F);
        if (moving) {
            loop(running ? StrawGolemAnimations.LEGS_RUN : StrawGolemAnimations.LEGS_WALK, idleTime);
        } else {
            loop(StrawGolemAnimations.LEGS_IDLE, idleTime);
        }

        if (running) {
            loop(StrawGolemAnimations.ARMS_SCARED, idleTime);
        } else if (golem.holdsItemAbove()) {
            loop(StrawGolemAnimations.ARMS_HOLD_BLOCK, idleTime);
        } else if (!golem.getMainHandItem().isEmpty()) {
            loop(StrawGolemAnimations.ARMS_HOLD_ITEM, idleTime);
        } else if (moving) {
            loop(StrawGolemAnimations.ARMS_WALK, idleTime);
        } else {
            loop(StrawGolemAnimations.ARMS_IDLE, idleTime);
        }
    }

    private void loop(AnimationDefinition definition, long time) {
        KeyframeAnimations.animate(this, definition, time, 1.0F, CACHE);
    }

    public void translateToBody(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        upper.translateAndRotate(poseStack);
        body.translateAndRotate(poseStack);
    }

    public void translateToItem(PoseStack poseStack) {
        root.translateAndRotate(poseStack);
        item.translateAndRotate(poseStack);
    }
}
