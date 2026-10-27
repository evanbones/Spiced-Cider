package com.evandev.spicedcider.compat.cirrus;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.jvn.cirrus.config.CirrusConfig;
import com.jvn.cirrus.config.CirrusConfigSpec;
import net.minecraft.network.chat.Component;

public final class CirrusSettings {

    public static Entry aurora() {
        return toggle("aurora", CirrusConfig.AURORA_ENABLED);
    }

    public static Entry shootingStars() {
        return toggle("shooting_stars", CirrusConfig.SHOOTING_STARS_ENABLED);
    }

    public static Entry customStars() {
        return toggle("custom_stars", CirrusConfig.CUSTOM_STARS_ENABLED);
    }

    private static Entry toggle(String key, CirrusConfigSpec.BooleanValue value) {
        return new SettingEntry(CycleSetting.toggle(Component.translatable("spicedcider.options." + key), value::get, enabled -> {
            value.set(enabled);
            CirrusConfig.SPEC.save();
        }).defaultValue(value.getDefault()).tooltip(Component.translatable("spicedcider.options." + key + ".tooltip")));
    }
}
