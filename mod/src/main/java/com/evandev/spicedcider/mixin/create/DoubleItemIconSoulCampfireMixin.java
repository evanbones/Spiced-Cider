package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@IfModLoaded("create")
@Mixin(targets = "com.simibubi.create.compat.jei.DoubleItemIcon", remap = false)
public class DoubleItemIconSoulCampfireMixin {
    @Shadow
    private Supplier<ItemStack> secondarySupplier;

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private void spicedcider$soulCampfireIcon(GuiGraphics graphics, int x, int y, CallbackInfo ci) {
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.emiCreateFanRework, false)) return;
        if (!secondarySupplier.get().is(Items.SOUL_CAMPFIRE)) return;
        PoseStack pose = graphics.pose();
        RenderSystem.enableDepthTest();
        pose.pushPose();
        pose.translate(x + 8.0F, y + 9.0F, 150.0F);
        pose.scale(11.0F, -11.0F, 11.0F);
        pose.mulPose(Axis.XP.rotationDegrees(30.0F));
        pose.mulPose(Axis.YP.rotationDegrees(225.0F));
        pose.translate(-0.5F, -0.5F, -0.5F);
        Lighting.setupFor3DItems();
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.SOUL_CAMPFIRE.defaultBlockState(), pose,
                graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        graphics.flush();
        Lighting.setupForFlatItems();
        pose.popPose();
        ci.cancel();
    }
}
