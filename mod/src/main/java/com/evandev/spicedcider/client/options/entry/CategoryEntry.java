package com.evandev.spicedcider.client.options.entry;

import com.evandev.spicedcider.client.options.SnapSink;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryEntry extends Entry {

    private static final int HEADER = 24;
    private static final int INDENT = 16;

    private final Component title;
    private final List<Entry> children;
    private final Set<String> collapsedCategories;
    private final String key;
    private final boolean collapsedByDefault;
    private Entry pressed;

    public CategoryEntry(Component title, List<Entry> children, Set<String> collapsedCategories) {
        this(title, children, collapsedCategories, false);
    }

    public CategoryEntry(Component title, List<Entry> children, Set<String> toggledCategories, boolean collapsedByDefault) {
        this.title = title;
        this.children = children;
        this.collapsedCategories = toggledCategories;
        this.key = title.getString();
        this.collapsedByDefault = collapsedByDefault;
    }

    public Component title() {
        return title;
    }

    public List<Entry> children() {
        return children;
    }

    private boolean collapsed() {
        return collapsedByDefault != collapsedCategories.contains(key);
    }

    @Override
    public int height() {
        int height = HEADER;
        if (!collapsed()) {
            for (Entry child : children) height += child.height();
        }
        return height;
    }

    private boolean headerHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + HEADER;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int color = headerHovered(mouseX, mouseY) ? TEXT_HOVER : TEXT_DIM;
        drawArrow(graphics, x, y + 12, collapsed(), color);
        graphics.drawString(font, title, x + 10, y + 12, color);

        if (collapsed()) return;
        int childY = y + HEADER;
        for (Entry child : children) {
            child.setBounds(x + INDENT, childY, width - INDENT);
            if (childY + child.height() >= 0 && childY <= graphics.guiHeight()) {
                child.render(graphics, mouseX, mouseY, partialTick);
            }
            childY += child.height();
        }
    }

    private static void drawArrow(GuiGraphics graphics, int x, int y, boolean collapsed, int color) {
        if (collapsed) {
            for (int i = 0; i < 4; i++) graphics.fill(x + i, y + i, x + i + 1, y + 7 - i, color);
        } else {
            for (int i = 0; i < 4; i++) graphics.fill(x + i, y + 1 + i, x + 7 - i, y + 2 + i, color);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (headerHovered(mouseX, mouseY)) {
            if (!collapsedCategories.remove(key)) collapsedCategories.add(key);
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        if (collapsed()) return false;
        for (Entry child : children) {
            if (child.isMouseOver(mouseX, mouseY) && child.mouseClicked(mouseX, mouseY, button)) {
                pressed = child;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (pressed != null) {
            Entry entry = pressed;
            pressed = null;
            return entry.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return pressed != null && pressed.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Entry child : children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Entry child : children) {
            if (child.charTyped(codePoint, modifiers)) return true;
        }
        return false;
    }

    @Override
    public List<FormattedCharSequence> tooltip(int mouseX, int mouseY) {
        if (collapsed()) return List.of();
        for (Entry child : children) {
            if (child.isMouseOver(mouseX, mouseY)) return child.tooltip(mouseX, mouseY);
        }
        return List.of();
    }

    @Override
    public void tick() {
        for (Entry child : children) child.tick();
    }

    @Override
    public void collectSnapPoints(SnapSink sink) {
        sink.accept(x + 4 + font.width(title) / 2, y + 16, 10);
        if (collapsed()) return;
        for (Entry child : children) child.collectSnapPoints(sink);
    }

    public CategoryEntry filter(String query, Component title) {
        List<Entry> matches = new ArrayList<>();
        for (Entry child : children) {
            if (child.matches(query)) matches.add(child);
        }
        return matches.isEmpty() ? null : new CategoryEntry(title, matches, collapsedCategories);
    }
}
