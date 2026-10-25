package com.evandev.spicedcider.client.options.setting;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class ValueText {

    public static Component strip(Component message, Component caption) {
        if (message.getContents() instanceof TranslatableContents contents) {
            Object[] args = contents.getArgs();
            switch (contents.getKey()) {
                case "options.generic_value" -> {
                    if (args.length == 2) return arg(args[1]);
                }
                case "options.percent_value" -> {
                    if (args.length == 2) return Component.translatable("spicedcider.options.value.percent", args[1]);
                }
                case "options.percent_add_value" -> {
                    if (args.length == 2) return Component.translatable("spicedcider.options.value.percent_add", args[1]);
                }
                case "options.pixel_value" -> {
                    if (args.length == 2) return Component.translatable("spicedcider.options.value.pixels", args[1]);
                }
                case "options.on.composed" -> {
                    return CommonComponents.OPTION_ON;
                }
                case "options.off.composed" -> {
                    return CommonComponents.OPTION_OFF;
                }
                default -> {
                }
            }
        }

        String text = message.getString();
        String prefix = caption.getString() + ": ";
        if (text.startsWith(prefix)) {
            return Component.literal(text.substring(prefix.length())).withStyle(message.getStyle());
        }
        return message;
    }

    private static Component arg(Object arg) {
        return arg instanceof Component component ? component : Component.literal(String.valueOf(arg));
    }
}
