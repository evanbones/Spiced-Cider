package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.content.item.BowFatigue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererBowFatigueMixin {
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void spicedcider$wobble(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress,
                                    ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (!(stack.getItem() instanceof BowItem) || stack.getUseAnimation() != UseAnim.BOW || !player.isUsingItem()
                || player.getUsedItemHand() != hand || player.getUseItemRemainingTicks() <= 0 || !BowFatigue.enabled()) return;
        float drawTicks = stack.getUseDuration(player) - (player.getUseItemRemainingTicks() - partialTicks + 1.0F);
        float power = drawTicks / 20.0F;
        power = Math.min((power * power + power * 2.0F) / 3.0F, 1.0F);
        if (power <= 0.1F) return;
        float bob = Mth.sin((drawTicks - 0.1F) * 1.3F) * (BowFatigue.fatigue((int) drawTicks) - power);
        poseStack.translate(0.0F, bob * 0.004F, 0.0F);
    }
}
