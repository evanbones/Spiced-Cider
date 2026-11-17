package com.evandev.spicedcider.client.models.sporal;

import com.evandev.spicedcider.entities.sporal.Sporal;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SporalModel extends HierarchicalModel<Sporal> {
    private static final long LEAP_POSE_MILLIS = (long) (SporalAnimations.LEAP.lengthInSeconds() * 1000.0F);
    private static final String[] PETALS = {"petal_0", "petal_1", "petal_2", "petal_3"};

    private final ModelPart root;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart upper;
    private final ModelPart head;
    private final ModelPart petals;
    private final ModelPart[] petalLeaves = new ModelPart[PETALS.length];
    private final Vector3f animationCache = new Vector3f();

    public SporalModel(ModelPart root) {
        this.root = root;
        this.leftLeg = root.getChild("leg1");
        this.rightLeg = root.getChild("leg2");
        this.upper = root.getChild("upper");
        this.head = this.upper.getChild("head");
        this.petals = this.upper.getChild("petals");
        for (int i = 0; i < PETALS.length; i++) {
            this.petalLeaves[i] = this.petals.getChild(PETALS[i]).getChild("leaf");
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 46).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F), PartPose.offset(-2.0F, 4.0F, 0.0F));
        root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 46).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 20.0F, 2.0F), PartPose.offset(2.0F, 4.0F, 0.0F));

        PartDefinition upper = root.addOrReplaceChild("upper", CubeListBuilder.create()
                .texOffs(32, 30).addBox(-4.0F, -14.0F, -2.0F, 8.0F, 14.0F, 4.0F)
                .texOffs(0, 20).addBox(-5.0F, -13.9F, -5.0F, 10.0F, 0.0F, 10.0F), PartPose.offset(0.0F, 4.0F, 0.0F));

        upper.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 30).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition petals = upper.addOrReplaceChild("petals", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -14.0F, 0.0F, 0.0F, -90.0F * Mth.DEG_TO_RAD, 0.0F));
        addPetal(petals, PETALS[0], -45.0F, -1.7F, -2.1446F, -15.10333F);
        addPetal(petals, PETALS[1], 135.0F, -1.7F, -2.1446F, -15.10333F);
        addPetal(petals, PETALS[2], 45.0F, -0.7F, -2.56722F, -16.00964F);
        addPetal(petals, PETALS[3], -135.0F, -0.7F, -2.56722F, -16.00964F);

        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void addPetal(PartDefinition petals, String name, float yaw, float pivotZ, float leafY, float leafZ) {
        PartDefinition petal = petals.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, yaw * Mth.DEG_TO_RAD, 0.0F));
        petal.addOrReplaceChild("leaf", CubeListBuilder.create().texOffs(3, 0).addBox(-10.0F, leafY, leafZ, 20.0F, 0.0F, 15.0F), PartPose.offsetAndRotation(0.0F, 0.0F, pivotZ, 25.0F * Mth.DEG_TO_RAD, 0.0F, 0.0F));
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(@NotNull Sporal entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;

        float idleSway = Mth.sin(ageInTicks * 0.05F);
        this.upper.zRot += idleSway * 0.03F;
        this.head.zRot -= idleSway * 0.04F;
        this.petals.yRot += Mth.sin(ageInTicks * 0.03F) * 0.12F;

        float leap = entity.getLeapBlend(ageInTicks - entity.tickCount);
        leap = leap * leap * (3.0F - 2.0F * leap);

        float stride = this.animateStride(limbSwing, Math.min(limbSwingAmount, 1.0F) * (1.0F - leap));
        float petalLift = Mth.lerp(leap, stride, 0.45F);

        for (int i = 0; i < this.petalLeaves.length; i++) {
            float flutter = Mth.sin(ageInTicks * 0.12F + i * Mth.HALF_PI) * 0.06F;
            this.petalLeaves[i].xRot -= petalLift + flutter;
        }

        this.animate(entity.bestowAnimationState, SporalAnimations.BESTOW, ageInTicks);
        if (leap > 0.0F) {
            KeyframeAnimations.animate(this, SporalAnimations.LEAP, LEAP_POSE_MILLIS, leap, this.animationCache);
        }
    }

    private float animateStride(float limbSwing, float amount) {
        float stride = limbSwing * 0.45F;
        this.leftLeg.xRot = Mth.cos(stride) * 0.5F * amount;
        this.rightLeg.xRot = Mth.cos(stride + Mth.PI) * 0.5F * amount;

        float lift = Math.abs(Mth.sin(stride)) * 0.8F * amount;
        this.leftLeg.y -= lift;
        this.rightLeg.y -= lift;
        this.upper.y -= lift;

        this.upper.xRot += 0.08F * amount;
        this.upper.zRot += Mth.sin(stride) * 0.06F * amount;
        return 0.2F * amount;
    }
}
