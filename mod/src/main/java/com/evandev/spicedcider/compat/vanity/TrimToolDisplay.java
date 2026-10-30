package com.evandev.spicedcider.compat.vanity;

import com.evandev.spicedcider.client.tooltrims.ToolTrimModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import tech.thatgravyboat.vanity.api.style.Style;
import tech.thatgravyboat.vanity.client.components.display.Display;

public class TrimToolDisplay implements Display {
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, float rotation) {
        if (this.stack.isEmpty()) return;
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer renderer = minecraft.getItemRenderer();
        BakedModel model = ToolTrimModels.atCallSite(renderer.getModel(this.stack, minecraft.level, null, 0), renderer, this.stack, minecraft.level, null, 0);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + width * 0.5, y + height * 0.5, 150.0);
        pose.mulPose(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
        pose.scale(40.0F, 40.0F, 40.0F);
        pose.mulPose(Axis.YN.rotationDegrees(rotation));
        renderer.render(this.stack, ItemDisplayContext.NONE, false, pose, graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, model);
        pose.popPose();
        graphics.flush();
    }

    @Override
    public void setValue(ItemStack stack, Style style) {
        this.stack = stack.copyWithCount(1);
    }
}
