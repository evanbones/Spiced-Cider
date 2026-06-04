package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfArmorLayer.class)
public class WolfArmorLayerMixin {

    @Shadow
    @Final
    private WolfModel<Wolf> model;

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    private void cider$renderHorseArmorAsWolfArmor(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Wolf wolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        ItemStack stack = wolf.getBodyArmorItem();

        if (stack.getItem() instanceof AnimalArmorItem armorItem && armorItem.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
            WolfArmorLayer layer = (WolfArmorLayer) (Object) this;

            layer.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(wolf, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(wolf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            String path = armorItem.getTexture().getPath();
            String rawFileName = path.substring(path.lastIndexOf('/') + 1);
            String cleanFileName = rawFileName.replace("horse_armor_", "");
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/entity/wolf/armor/" + cleanFileName);

            int renderColor = -1;

            if (stack.is(ItemTags.DYEABLE)) {
                int rgb = DyedItemColor.getOrDefault(stack, 10511680);
                renderColor = rgb | 0xFF000000;
            }

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, renderColor);

            ci.cancel();
        }
    }
}