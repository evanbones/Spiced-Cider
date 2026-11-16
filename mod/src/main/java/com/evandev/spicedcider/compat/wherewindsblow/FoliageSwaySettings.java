package com.evandev.spicedcider.compat.wherewindsblow;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.jvn.wherewindsblow.config.ClientConfig;
import net.minecraft.network.chat.Component;

public final class FoliageSwaySettings {

    public static Entry foliageSway() {
        return new SettingEntry(CycleSetting.toggle(Component.translatable("spicedcider.options.foliage_sway"), ClientConfig.ENABLE_WIND_FOLIAGE_SWAY::get, enabled -> {
            ClientConfig.ENABLE_WIND_FOLIAGE_SWAY.set(enabled);
            ClientConfig.ENABLE_WIND_FOLIAGE_SWAY.save();
        }).defaultValue(ClientConfig.ENABLE_WIND_FOLIAGE_SWAY.getDefault())
                .tooltip(Component.translatable("spicedcider.options.foliage_sway.tooltip")));
    }
}
