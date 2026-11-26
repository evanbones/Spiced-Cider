package com.evandev.spicedcider.client.options.setting;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class QualityColors {

    @Nullable
    public static ChatFormatting of(@Nullable Object value) {
        if (value instanceof Boolean enabled) return enabled ? ChatFormatting.GREEN : ChatFormatting.RED;
        if (!(value instanceof Enum<?> constant)) return null;
        return switch (constant.name().toUpperCase(Locale.ROOT)) {
            case "OFF", "NONE", "MINIMAL", "DISABLED" -> ChatFormatting.RED;
            case "FASTEST", "LOW" -> ChatFormatting.GOLD;
            case "FAST", "DECREASED", "MEDIUM" -> ChatFormatting.YELLOW;
            case "FANCY", "ALL", "ON", "HIGH", "ENABLED", "FULL" -> ChatFormatting.GREEN;
            case "FABULOUS", "ULTRA" -> ChatFormatting.AQUA;
            default -> null;
        };
    }

    public static void apply(AbstractWidget widget) {
        if (!(widget instanceof CycleButton<?> cycle)) return;
        Component message = cycle.getMessage();
        if (message.getStyle().getColor() != null) return;
        ChatFormatting color = of(cycle.getValue());
        if (color != null) cycle.setMessage(message.copy().withStyle(color));
    }
}
