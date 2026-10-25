package com.evandev.spicedcider.client.options;

import com.evandev.spicedcider.client.options.entry.Entry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public record SettingsPage(String id, Component title, ItemStack icon, boolean searchable, BooleanSupplier visible, Function<SettingsScreen, List<Entry>> builder) {

    public SettingsPage(String id, ItemStack icon, boolean searchable, Function<SettingsScreen, List<Entry>> builder) {
        this(id, Component.translatable("spicedcider.options.page." + id), icon, searchable, () -> true, builder);
    }
}
