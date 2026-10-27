package com.evandev.spicedcider.client.nether;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class NetherSettings {

    public static Entry heatHaze() {
        return toggle("heat_haze", SpicedCiderConfig.CLIENT.netherHeatHaze);
    }

    public static Entry mist() {
        return toggle("nether_mist", SpicedCiderConfig.CLIENT.netherMist);
    }

    private static Entry toggle(String key, ModConfigSpec.BooleanValue value) {
        return new SettingEntry(CycleSetting.toggle(Component.translatable("spicedcider.options." + key), value::get, enabled -> {
            value.set(enabled);
            value.save();
        }).defaultValue(value.getDefault()).tooltip(Component.translatable("spicedcider.options." + key + ".tooltip")));
    }
}
