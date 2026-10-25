package com.evandev.spicedcider.client.options.entry;

import com.evandev.spicedcider.client.options.SnapSink;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.FormattedText;

import java.util.List;
import java.util.Locale;

public class PackEntry extends Entry {

    private static final ResourceLocation SELECT_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("transferable_list/select_highlighted");
    private static final ResourceLocation SELECT = ResourceLocation.withDefaultNamespace("transferable_list/select");
    private static final ResourceLocation UNSELECT_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("transferable_list/unselect_highlighted");
    private static final ResourceLocation UNSELECT = ResourceLocation.withDefaultNamespace("transferable_list/unselect");
    private static final ResourceLocation MOVE_UP_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("transferable_list/move_up_highlighted");
    private static final ResourceLocation MOVE_UP = ResourceLocation.withDefaultNamespace("transferable_list/move_up");
    private static final ResourceLocation MOVE_DOWN_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("transferable_list/move_down_highlighted");
    private static final ResourceLocation MOVE_DOWN = ResourceLocation.withDefaultNamespace("transferable_list/move_down");

    private final PackSelectionModel.Entry pack;

    public PackEntry(PackSelectionModel.Entry pack) {
        this.pack = pack;
    }

    @Override
    public int height() {
        return 36;
    }

    private boolean canInteract() {
        return pack.canSelect() || pack.canUnselect() || pack.canMoveUp() || pack.canMoveDown();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isMouseOver(mouseX, mouseY);
        if (hovered) {
            graphics.fill(x - 2, y, x + width, y + 34, 0x20FFFFFF);
        }

        graphics.blit(pack.getIconTexture(), x, y + 1, 0.0F, 0.0F, 32, 32, 32, 32);

        boolean compatible = pack.getCompatibility().isCompatible();
        Component title = pack.getTitle();
        int textX = x + 36;
        int textWidth = width - 38;
        FormattedText clipped = font.substrByWidth(title, textWidth);
        graphics.drawString(font, net.minecraft.locale.Language.getInstance().getVisualOrder(clipped), textX, y + 2, compatible ? TEXT : 0xFFFF5555);

        Component description = compatible ? pack.getDescription() : pack.getCompatibility().getDescription();
        List<FormattedCharSequence> lines = font.split(description, textWidth);
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            graphics.drawString(font, lines.get(i), textX, y + 13 + i * 10, 0xFF808080);
        }

        if (hovered && canInteract()) {
            graphics.fill(x, y + 1, x + 32, y + 33, 0xA0909090);
            int relX = mouseX - x;
            int relY = mouseY - y - 1;
            if (pack.canSelect()) {
                graphics.blitSprite(relX < 32 ? SELECT_HIGHLIGHTED : SELECT, x, y + 1, 32, 32);
            } else {
                if (pack.canUnselect()) {
                    graphics.blitSprite(relX < 16 ? UNSELECT_HIGHLIGHTED : UNSELECT, x, y + 1, 32, 32);
                }
                if (pack.canMoveUp()) {
                    graphics.blitSprite(relX < 32 && relX > 16 && relY < 16 ? MOVE_UP_HIGHLIGHTED : MOVE_UP, x, y + 1, 32, 32);
                }
                if (pack.canMoveDown()) {
                    graphics.blitSprite(relX < 32 && relX > 16 && relY > 16 ? MOVE_DOWN_HIGHLIGHTED : MOVE_DOWN, x, y + 1, 32, 32);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double relX = mouseX - x;
        double relY = mouseY - y - 1;
        if (relX < 0 || relX > 32 || button != 0) return false;
        if (pack.canSelect()) {
            pack.select();
        } else if (relX < 16 && pack.canUnselect()) {
            pack.unselect();
        } else if (relX > 16 && relY < 16 && pack.canMoveUp()) {
            pack.moveUp();
        } else if (relX > 16 && relY > 16 && pack.canMoveDown()) {
            pack.moveDown();
        } else {
            return false;
        }
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }

    @Override
    public void collectSnapPoints(SnapSink sink) {
        sink.accept(x + 16, y + 17, 16);
    }

    @Override
    public List<FormattedCharSequence> tooltip(int mouseX, int mouseY) {
        if (mouseX >= x + 36 && font.width(pack.getTitle()) > width - 38) {
            return font.split(pack.getTitle().copy().withStyle(ChatFormatting.WHITE), 250);
        }
        return List.of();
    }

    @Override
    public boolean matches(String query) {
        return pack.getTitle().getString().toLowerCase(Locale.ROOT).contains(query);
    }
}
