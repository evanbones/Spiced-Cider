package com.evandev.spicedcider.client.options.entry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ResetButton extends Button {

    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "options/reset");
    private static final ResourceLocation ICON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "options/reset_highlighted");
    private static final ResourceLocation ICON_DISABLED = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "options/reset_disabled");

    public ResetButton(OnPress onPress) {
        super(0, 0, 20, 20, Component.empty(), onPress, DEFAULT_NARRATION);
        setTooltip(Tooltip.create(Component.translatable("spicedcider.options.reset")));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        ResourceLocation icon = !active ? ICON_DISABLED : isHoveredOrFocused() ? ICON_HIGHLIGHTED : ICON;
        graphics.blitSprite(icon, getX(), getY(), 20, 20);
    }
}
