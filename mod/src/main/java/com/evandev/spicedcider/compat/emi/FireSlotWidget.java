package com.evandev.spicedcider.compat.emi;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.List;

public class FireSlotWidget extends SlotWidget {
    private static final ResourceLocation FIRE = ResourceLocation.withDefaultNamespace("block/fire_0");

    public FireSlotWidget(SlotWidget slot) {
        super(EmiStack.EMPTY, slot.getBounds().x(), slot.getBounds().y());
    }

    @Override
    public void drawBackground(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        super.drawBackground(draw, mouseX, mouseY, delta);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(FIRE);
        draw.blit(getBounds().x() + 1, getBounds().y() + 1, 0, 16, 16, sprite);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        return List.of(ClientTooltipComponent.create(Component.translatable("block.minecraft.fire").getVisualOrderText()));
    }
}
