package com.evandev.spicedcider.client.tips;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Tips {

    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("spicedcider/tips.txt");
    private static final long SHOW_MILLIS = 10_000L;
    private static final long FORGET_MILLIS = 2_000L;
    private static final int MARGIN = 8;
    private static final int MAX_WIDTH = 240;
    private static final RandomSource RANDOM = RandomSource.create();

    private static List<String> tips = List.of();
    private static long loadedModified = Long.MIN_VALUE;
    private static int current = -1;
    private static long shownSince;
    private static long lastSeen;

    @Nullable
    public static Component current() {
        long now = Util.getMillis();
        if (current < 0 || now - lastSeen > FORGET_MILLIS || now - shownSince > SHOW_MILLIS) {
            List<String> all = load();
            if (all.isEmpty()) return null;
            int next = RANDOM.nextInt(all.size());
            if (all.size() > 1 && next == current) {
                next = (next + 1) % all.size();
            }
            current = next;
            shownSince = now;
        }
        lastSeen = now;
        return current < tips.size() ? Component.literal(tips.get(current)) : null;
    }

    public static void render(GuiGraphics graphics, Font font, int bottom, int maxWidth) {
        Component tip = current();
        if (tip == null) return;
        List<FormattedCharSequence> lines = font.split(tip, Math.min(MAX_WIDTH, maxWidth));
        int y = bottom - lines.size() * font.lineHeight;
        graphics.drawString(font, Component.translatable("gui.spicedcider.tip").withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE), MARGIN, y - font.lineHeight - 2, 0xFFFFFF);
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, MARGIN, y, 0xE0E0E0);
            y += font.lineHeight;
        }
    }

    public static void renderBottomLeft(GuiGraphics graphics, Font font, int width, int height) {
        render(graphics, font, height - MARGIN, width - MARGIN * 2);
    }

    private static List<String> load() {
        long modified;
        try {
            modified = Files.getLastModifiedTime(FILE).toMillis();
        } catch (IOException e) {
            return tips = List.of();
        }
        if (modified != loadedModified) {
            loadedModified = modified;
            try {
                tips = Files.readAllLines(FILE, StandardCharsets.UTF_8).stream()
                        .map(String::strip)
                        .filter(line -> !line.isEmpty())
                        .toList();
            } catch (IOException e) {
                SpicedCider.LOGGER.warn("Could not read tips from {}", FILE, e);
                tips = List.of();
            }
            current = -1;
        }
        return tips;
    }
}
