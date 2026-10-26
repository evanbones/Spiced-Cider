package com.evandev.spicedcider.client.renderer;

import com.evandev.spicedcider.content.item.PocketJukeboxContents;
import com.evandev.spicedcider.content.item.PocketJukeboxTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PocketJukeboxTooltipRenderer implements ClientTooltipComponent {
    private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("container/bundle/background");
    private static final ResourceLocation SLOT = ResourceLocation.withDefaultNamespace("container/bundle/slot");
    private static final ResourceLocation BLOCKED_SLOT = ResourceLocation.withDefaultNamespace("container/bundle/blocked_slot");

    private final PocketJukeboxContents contents;

    public PocketJukeboxTooltipRenderer(PocketJukeboxTooltip tooltip) {
        this.contents = tooltip.contents();
    }

    @Override
    public int getHeight() {
        return rows() * 20 + 2 + 4;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return columns() * 18 + 2;
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics graphics) {
        int columns = columns();
        int rows = rows();
        graphics.blitSprite(BACKGROUND, x, y, columns * 18 + 2, rows * 20 + 2);
        boolean full = contents.size() >= PocketJukeboxContents.CAPACITY;
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int slotX = x + column * 18 + 1;
                int slotY = y + row * 20 + 1;
                if (index >= contents.size()) {
                    graphics.blitSprite(full ? BLOCKED_SLOT : SLOT, slotX, slotY, 0, 18, 20);
                } else {
                    ItemStack disc = contents.discs().get(index);
                    graphics.blitSprite(SLOT, slotX, slotY, 0, 18, 20);
                    graphics.renderItem(disc, slotX + 1, slotY + 1, index);
                    graphics.renderItemDecorations(font, disc, slotX + 1, slotY + 1);
                    if (index == contents.current()) AbstractContainerScreen.renderSlotHighlight(graphics, slotX + 1, slotY + 1, 0);
                }
                index++;
            }
        }
    }

    private int columns() {
        return Math.max(2, (int) Math.ceil(Math.sqrt(contents.size() + 1.0)));
    }

    private int rows() {
        return (int) Math.ceil((contents.size() + 1.0) / columns());
    }
}
