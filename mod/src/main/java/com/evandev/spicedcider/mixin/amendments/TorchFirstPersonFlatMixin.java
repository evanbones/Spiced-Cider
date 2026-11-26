package com.evandev.spicedcider.mixin.amendments;

import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.amendments.client.renderers.TorchRendererExtension;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("amendments")
@Mixin(value = TorchRendererExtension.class, remap = false)
public abstract class TorchFirstPersonFlatMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void spicedcider$flatInFirstPerson(AbstractClientPlayer player, ItemStack stack, InteractionHand hand, HumanoidArm arm,
                                               PoseStack poseStack, float partialTicks, float pitch, float attackAnim, float handHeight,
                                               MultiBufferSource buffer, int light, ItemInHandRenderer renderer,
                                               CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
