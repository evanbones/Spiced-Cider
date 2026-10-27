package com.evandev.spicedcider.client.renderer.sputter;

import com.evandev.spicedcider.client.models.sputter.SputterArrowfulModel;
import com.evandev.spicedcider.client.models.sputter.SputterModel;
import com.evandev.spicedcider.entities.sputter.SputterEntity;
import com.evandev.spicedcider.registry.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SputterArrowfulLayer extends RenderLayer<SputterEntity, SputterModel<SputterEntity>> {
    private static final ResourceLocation NORMAL_ARROW = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");
    private static final Map<Item, ResourceLocation> ARROW_TEXTURES = new HashMap<>();
    private final SputterArrowfulModel<SputterEntity> model;

    public SputterArrowfulLayer(RenderLayerParent<SputterEntity, SputterModel<SputterEntity>> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new SputterArrowfulModel<>(modelSet.bakeLayer(ModModelLayers.SPUTTER_ARROWFUL));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SputterEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) return;
        ResourceLocation texture = ARROW_TEXTURES.computeIfAbsent(stack.getItem(), item -> arrowTexture(entity, stack));
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, texture, poseStack, buffer, packedLight, entity,
                limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick, -1);
    }

    private static ResourceLocation arrowTexture(SputterEntity entity, ItemStack stack) {
        if (!(stack.getItem() instanceof ArrowItem item)) return NORMAL_ARROW;
        AbstractArrow arrow = item.createArrow(entity.level(), stack, entity, null);
        EntityRenderer<? super AbstractArrow> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(arrow);
        return renderer instanceof ArrowRenderer<?> ? renderer.getTextureLocation(arrow) : NORMAL_ARROW;
    }
}
