package com.evandev.spicedcider.mixin.connectiblechains;

import com.evandev.connectiblechains.client.ClientInitializer;
import com.evandev.connectiblechains.client.render.entity.ChainKnotEntityRenderer;
import com.evandev.connectiblechains.client.render.entity.model.ChainKnotEntityModel;
import com.evandev.connectiblechains.client.render.entity.state.ChainKnotEntityRenderState;
import com.evandev.connectiblechains.entity.ChainKnotEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("connectiblechains")
@Mixin(targets = "com.evandev.connectiblechains.client.render.entity.ChainKnotEntityRenderer", remap = false)
public class ChainKnotRendererLeadKnotMixin {
    @Unique
    private static final RenderType spicedcider$LEAD_KNOT = RenderType.entityCutout(ResourceLocation.withDefaultNamespace("textures/entity/lead_knot.png"));

    @Unique
    private LeashKnotModel<LeashFenceKnotEntity> spicedcider$leashKnot;

    @Unique
    private boolean spicedcider$drawingLead;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$bakeLeashKnot(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.spicedcider$leashKnot = new LeashKnotModel<>(context.bakeLayer(ModelLayers.LEASH_KNOT));
    }

    @WrapOperation(method = "render(Lcom/evandev/connectiblechains/entity/ChainKnotEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/evandev/connectiblechains/client/render/entity/ChainKnotEntityRenderer;getKnotRenderType(Lnet/minecraft/world/item/Item;)Lnet/minecraft/client/renderer/RenderType;"))
    private RenderType spicedcider$leadKnotRenderType(ChainKnotEntityRenderer renderer, Item item, Operation<RenderType> original) {
        ResourceLocation texture = ClientInitializer.getInstance().getChainTextureManager().getKnotTexture(item);
        spicedcider$drawingLead = texture.getNamespace().equals("connectiblechains") && texture.getPath().endsWith("/lead_knot.png");
        return spicedcider$drawingLead ? spicedcider$LEAD_KNOT : original.call(renderer, item);
    }

    @WrapOperation(method = "render(Lcom/evandev/connectiblechains/entity/ChainKnotEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/evandev/connectiblechains/client/render/entity/model/ChainKnotEntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private void spicedcider$leadKnotModel(ChainKnotEntityModel<?> model, PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int color, Operation<Void> original, @Local(argsOnly = true) ChainKnotEntity knot, @Local ChainKnotEntityRenderState state) {
        if (!spicedcider$drawingLead || spicedcider$leashKnot == null) {
            original.call(model, poseStack, consumer, light, overlay, color);
            return;
        }
        if (!knot.level().getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB(knot.blockPosition())).isEmpty()) return;
        poseStack.pushPose();
        poseStack.scale(1.0F / state.knotScaleXZ, 1.0F, 1.0F / state.knotScaleXZ);
        poseStack.translate(0.0F, -0.875F, 0.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        spicedcider$leashKnot.renderToBuffer(poseStack, consumer, light, overlay);
        poseStack.popPose();
    }
}
