package com.evandev.spicedcider.client.options.setting;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum Impact {
    LOW(ChatFormatting.GREEN),
    MEDIUM(ChatFormatting.YELLOW),
    HIGH(ChatFormatting.GOLD),
    VARIES(ChatFormatting.WHITE);

    private final ChatFormatting color;

    Impact(ChatFormatting color) {
        this.color = color;
    }

    public Component line() {
        Component value = Component.translatable("spicedcider.options.impact." + name().toLowerCase(Locale.ROOT)).withStyle(color);
        return Component.translatable("spicedcider.options.impact", value).withStyle(ChatFormatting.GRAY);
    }
}
