package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StructureNames {
    private static final Set<ResourceLocation> WARNED = new HashSet<>();

    public static String key(ResourceLocation id) {
        return "structure." + id.getNamespace() + "." + id.getPath().replace('/', '.');
    }

    public static Component name(ResourceLocation id) {
        String key = key(id);
        if (I18n.exists(key)) return Component.translatable(key);
        if (WARNED.add(id)) SpicedCider.LOGGER.warn("Missing structure name translation {}", key);
        return Component.literal(prettify(id, false));
    }

    public static String prettify(ResourceLocation id, boolean tag) {
        String last = id.getPath().substring(id.getPath().lastIndexOf('/') + 1);
        if (tag && last.startsWith("is_")) last = last.substring(3);
        return Arrays.stream(last.split("_"))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
