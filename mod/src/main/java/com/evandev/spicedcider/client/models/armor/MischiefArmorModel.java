package com.evandev.spicedcider.client.models.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MischiefArmorModel<T extends LivingEntity> extends HumanoidModel<T> {
    private static final Map<Integer, MischiefArmorModel<? extends LivingEntity>> CACHE = new HashMap<>();
    private final EquipmentSlot slot;
    private final byte entityFlag;
    public ModelPart helmet;
    public ModelPart chestplate1;
    public ModelPart chestplate2;
    public ModelPart leggingsLeft;
    public ModelPart leggingsRight;
    public ModelPart bootsLeft;
    public ModelPart bootsRight;
    public ModelPart shoulderPadRight;
    public ModelPart shoulderPadLeft;
    public ModelPart piglinHelmet1;
    public ModelPart piglinHelmet2;
    public ModelPart piglinHelmet3;
    public ModelPart piglinHelmet4;
    public ModelPart piglinHelmet5;
    public ModelPart illagerHelmet1;
    public ModelPart illagerHelmet2;
    public ModelPart illagerHelmet3;

    public MischiefArmorModel(int entityFlag) {
        this(entityFlag, createArmorLayer().bakeRoot());
    }

    public MischiefArmorModel(int entityFlag, ModelPart root) {
        super(root);

        this.slot = EquipmentSlot.values()[entityFlag & 15];
        this.entityFlag = (byte) (entityFlag >> 4);

        this.helmet = root.getChild("helmet");
        this.chestplate1 = root.getChild("chestplate1");
        this.chestplate2 = root.getChild("chestplate2");
        this.leggingsLeft = root.getChild("leggingsLeft");
        this.leggingsRight = root.getChild("leggingsRight");
        this.bootsLeft = root.getChild("bootsLeft");
        this.bootsRight = root.getChild("bootsRight");
        this.shoulderPadRight = root.getChild("shoulderPadRight");
        this.shoulderPadLeft = root.getChild("shoulderPadLeft");
        this.piglinHelmet1 = root.getChild("piglinHelmet1");
        this.piglinHelmet2 = root.getChild("piglinHelmet2");
        this.piglinHelmet3 = root.getChild("piglinHelmet3");
        this.piglinHelmet4 = root.getChild("piglinHelmet4");
        this.piglinHelmet5 = root.getChild("piglinHelmet5");
        this.illagerHelmet1 = root.getChild("illagerHelmet1");
        this.illagerHelmet2 = root.getChild("illagerHelmet2");
        this.illagerHelmet3 = root.getChild("illagerHelmet3");
    }

    public static LayerDefinition createArmorLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = meshdefinition.getRoot();
        root.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.6F)).texOffs(36, 0).addBox(-1.0F, -11.0F, -6.1F, 2.0F, 8.0F, 12.0F, new CubeDeformation(0.125F)), PartPose.ZERO);
        root.addOrReplaceChild("chestplate1", CubeListBuilder.create().texOffs(0, 17).addBox(-4.5F, 0.0F, -2.5F, 9.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
        root.addOrReplaceChild("chestplate2", CubeListBuilder.create().texOffs(0, 34).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 15.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.ZERO);
        root.addOrReplaceChild("leggingsLeft", CubeListBuilder.create().texOffs(48, 50).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("leggingsRight", CubeListBuilder.create().texOffs(48, 50).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bootsLeft", CubeListBuilder.create().texOffs(32, 48).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("bootsRight", CubeListBuilder.create().texOffs(32, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("shoulderPadRight", CubeListBuilder.create().texOffs(42, 20).addBox(-4.0F, -2.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(-5.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("shoulderPadLeft", CubeListBuilder.create().texOffs(42, 20).mirror().addBox(-1.0F, -2.0F, -3.0F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(5.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("piglinHelmet1", CubeListBuilder.create().texOffs(64, 0).addBox(-5.0F, -8.5F, -4.0F, 10.0F, 9.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.ZERO);
        root.addOrReplaceChild("piglinHelmet2", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, -12.5F, -6.1F, 2.0F, 8.0F, 12.0F, new CubeDeformation(0.125F)), PartPose.ZERO);
        root.addOrReplaceChild("piglinHelmet3", CubeListBuilder.create().texOffs(64, 20).addBox(-2.0F, -4.2F, -5.7F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.35F)), PartPose.ZERO);
        root.addOrReplaceChild("piglinHelmet4", CubeListBuilder.create().texOffs(64, 25).addBox(2.5F, -2.0F, -5.9F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.15F)), PartPose.ZERO);
        root.addOrReplaceChild("piglinHelmet5", CubeListBuilder.create().texOffs(64, 25).addBox(-3.5F, -2.0F, -5.9F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.15F)), PartPose.ZERO);
        root.addOrReplaceChild("illagerHelmet1", CubeListBuilder.create().texOffs(64, 28).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.ZERO);
        root.addOrReplaceChild("illagerHelmet2", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, -13.0F, -6.1F, 2.0F, 8.0F, 12.0F, new CubeDeformation(0.125F)), PartPose.ZERO);
        root.addOrReplaceChild("illagerHelmet3", CubeListBuilder.create().texOffs(64, 30).addBox(-1.0F, -3.0F, -6.1F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.125F)), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public static HumanoidModel<?> getModel(EquipmentSlot slot, LivingEntity entity) {
        boolean illager = entity instanceof AbstractIllager
                || entity instanceof ZombieVillager
                || entity instanceof AbstractVillager;
        boolean piglin = entity instanceof AbstractPiglin || entity instanceof ZombifiedPiglin;
        int entityFlag = (slot.ordinal() & 15) | (illager ? 1 : 0) << 4 | (piglin ? 1 : 0) << 5 | (entity.isBaby() ? 1 : 0) << 6;
        return CACHE.computeIfAbsent(entityFlag, MischiefArmorModel::new);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        boolean illager = (this.entityFlag & 1) > 0;
        boolean piglin = (this.entityFlag & 2) > 0;
        boolean child = (this.entityFlag & 4) > 0;

        if (this.slot == EquipmentSlot.HEAD) {
            if (piglin) {
                poseStack.pushPose();
                this.piglinHelmet1.copyFrom(this.head);
                this.piglinHelmet2.copyFrom(this.head);
                this.piglinHelmet3.copyFrom(this.head);
                this.piglinHelmet4.copyFrom(this.head);
                this.piglinHelmet5.copyFrom(this.head);
                if (child) {
                    poseStack.scale(0.8F, 0.8F, 0.8F);
                    this.piglinHelmet1.setPos(0.0F, 15.0F, 0.0F);
                    this.piglinHelmet2.setPos(0.0F, 15.0F, 0.0F);
                    this.piglinHelmet3.setPos(0.0F, 15.0F, 0.0F);
                    this.piglinHelmet4.setPos(0.0F, 15.0F, 0.0F);
                    this.piglinHelmet5.setPos(0.0F, 15.0F, 0.0F);
                }
                this.piglinHelmet1.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.piglinHelmet2.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.piglinHelmet3.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.piglinHelmet4.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.piglinHelmet5.render(poseStack, buffer, packedLight, packedOverlay, color);
                poseStack.popPose();
            } else if (illager) {
                poseStack.pushPose();
                this.illagerHelmet1.copyFrom(this.head);
                this.illagerHelmet2.copyFrom(this.head);
                this.illagerHelmet3.copyFrom(this.head);
                if (child) {
                    poseStack.scale(0.8F, 0.8F, 0.8F);
                    this.illagerHelmet1.setPos(0.0F, 15.0F, 0.0F);
                    this.illagerHelmet2.setPos(0.0F, 15.0F, 0.0F);
                    this.illagerHelmet3.setPos(0.0F, 15.0F, 0.0F);
                }
                this.illagerHelmet1.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.illagerHelmet2.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.illagerHelmet3.render(poseStack, buffer, packedLight, packedOverlay, color);
                poseStack.popPose();
            } else {
                poseStack.pushPose();
                this.helmet.copyFrom(this.head);
                if (child) {
                    poseStack.scale(0.8F, 0.8F, 0.8F);
                    this.helmet.setPos(0.0F, 15.0F, 0.0F);
                }
                this.helmet.render(poseStack, buffer, packedLight, packedOverlay, color);
                poseStack.popPose();
            }
        }

        if (this.slot == EquipmentSlot.CHEST) {
            poseStack.pushPose();

            this.chestplate1.copyFrom(this.body);
            this.shoulderPadLeft.copyFrom(this.leftArm);
            this.shoulderPadRight.copyFrom(this.rightArm);
            if (child) {
                poseStack.scale(0.5F, 0.5F, 0.5F);
                this.chestplate1.setPos(0.0F, 24.0F, 0.0F);
                this.shoulderPadLeft.setPos(5.0F, 24.0F, 0.0F);
                this.shoulderPadRight.setPos(-5.0F, 24.0F, 0.0F);
            }
            this.shoulderPadLeft.render(poseStack, buffer, packedLight, packedOverlay, color);
            this.shoulderPadRight.render(poseStack, buffer, packedLight, packedOverlay, color);
            if (illager) {
                poseStack.scale(1.0F, 1.0F, 1.3F);
            }
            this.chestplate1.render(poseStack, buffer, packedLight, packedOverlay, color);
            poseStack.popPose();
        }

        if (this.slot == EquipmentSlot.LEGS) {
            poseStack.pushPose();
            poseStack.scale(1.01F, 1.0F, 1.01F);
            this.chestplate2.copyFrom(this.body);
            this.leggingsLeft.copyFrom(this.leftLeg);
            this.leggingsRight.copyFrom(this.rightLeg);
            if (child) {
                poseStack.scale(0.5F, 0.5F, 0.5F);
                this.leggingsLeft.setPos(2.0F, 36.0F, 0.0F);
                this.leggingsRight.setPos(-2.0F, 36.0F, 0.0F);
                this.chestplate2.setPos(0.0F, 24.0F, 0.0F);
            }
            this.leggingsLeft.render(poseStack, buffer, packedLight, packedOverlay, color);
            this.leggingsRight.render(poseStack, buffer, packedLight, packedOverlay, color);
            if (illager) {
                poseStack.scale(1.0F, 1.0F, 1.32F);
            }
            this.chestplate2.render(poseStack, buffer, packedLight, packedOverlay, color);
            poseStack.popPose();
        }

        if (this.slot == EquipmentSlot.FEET) {
            poseStack.pushPose();
            poseStack.scale(1.05F, 1.0F, 1.05F);

            this.bootsLeft.copyFrom(this.leftLeg);
            this.bootsRight.copyFrom(this.rightLeg);
            if (child) {
                poseStack.scale(0.5F, 0.5F, 0.5F);
                this.bootsLeft.setPos(2.0F, 37.0F, 0.0F);
                this.bootsRight.setPos(-2.0F, 37.0F, 0.0F);
            }
            this.bootsLeft.render(poseStack, buffer, packedLight, packedOverlay, color);
            this.bootsRight.render(poseStack, buffer, packedLight, packedOverlay, color);
            poseStack.popPose();
        }
    }
}
