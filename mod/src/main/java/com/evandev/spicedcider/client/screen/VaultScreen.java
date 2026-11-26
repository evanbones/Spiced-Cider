package com.evandev.spicedcider.client.screen;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.vault.VaultMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.fml.ModList;

public class VaultScreen extends AbstractContainerScreen<VaultMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/gui/container/item_vault.png");
    private static final ResourceLocation SCROLLER = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    private static final ResourceLocation SCROLLER_DISABLED = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");
    private static final int TRACK_X = 174;
    private static final int TRACK_Y = 18;
    private static final int TRACK_HEIGHT = 108;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int COUNT_RIGHT = ModList.get().isLoaded("quark") ? 128 : 178;

    private boolean scrolling;
    private float scrollOffset;

    public VaultScreen(VaultMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 194;
        this.imageHeight = 222;
        this.inventoryLabelX = 17;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        Component count = Component.translatable("container.spicedcider.item_vault.slots", this.menu.usedSlots(), this.menu.vaultSlots());
        graphics.drawString(this.font, count, COUNT_RIGHT - this.font.width(count), this.inventoryLabelY, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        for (int i = 0; i < this.menu.vaultSlots(); i++) {
            Slot slot = this.menu.slots.get(i);
            if (slot.isActive()) {
                graphics.blit(TEXTURE, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, 200, 0, 18, 18);
            }
        }
        int scrollerY = this.topPos + TRACK_Y + (int) ((TRACK_HEIGHT - SCROLLER_HEIGHT) * this.scrollOffset);
        graphics.blitSprite(this.menu.canScroll() ? SCROLLER : SCROLLER_DISABLED, this.leftPos + TRACK_X, scrollerY, 12, SCROLLER_HEIGHT);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.menu.canScroll()) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        this.scrollOffset = this.menu.scrollBy(this.scrollOffset, scrollY);
        this.menu.scrollTo(this.scrollOffset);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && insideScrollbar(mouseX, mouseY)) {
            this.scrolling = this.menu.canScroll();
            if (this.scrolling) dragTo(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling) {
            dragTo(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) this.scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        int row = this.menu.scrollRow;
        super.resize(minecraft, width, height);
        this.scrollOffset = this.menu.scrollForRow(row);
        this.menu.scrollTo(this.scrollOffset);
    }

    private void dragTo(double mouseY) {
        int top = this.topPos + TRACK_Y;
        this.scrollOffset = Mth.clamp(((float) mouseY - top - SCROLLER_HEIGHT / 2.0F) / (TRACK_HEIGHT - SCROLLER_HEIGHT), 0.0F, 1.0F);
        this.menu.scrollTo(this.scrollOffset);
    }

    private boolean insideScrollbar(double mouseX, double mouseY) {
        int x = this.leftPos + TRACK_X;
        int y = this.topPos + TRACK_Y;
        return mouseX >= x && mouseX < x + 14 && mouseY >= y && mouseY < y + TRACK_HEIGHT;
    }
}
