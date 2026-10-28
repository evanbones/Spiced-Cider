package com.evandev.spicedcider.compat.manuallabour;

import com.evandev.manual_labour.client.renderer.ToolAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

public class SpoonStirRender {

    public static void render(ItemStack spoon, float angle, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        ToolAnimations.renderLadleStirring(spoon, angle, poseStack, buffer, light, overlay);
    }
}
