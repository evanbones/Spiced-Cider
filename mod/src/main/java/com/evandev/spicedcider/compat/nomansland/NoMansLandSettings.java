package com.evandev.spicedcider.compat.nomansland;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.farcr.nomansland.NMLConfig;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class NoMansLandSettings {

    public static Entry sunDogs() {
        return toggle("sun_dogs", SpicedCiderConfig.CLIENT.sunDogs);
    }

    public static Entry invertedBellBlur() {
        return toggle("inverted_bell_blur", NMLConfig.INVERTED_BELL_BLUR);
    }

    private static Entry toggle(String key, ModConfigSpec.BooleanValue value) {
        return new SettingEntry(CycleSetting.toggle(Component.translatable("spicedcider.options." + key), value::get, enabled -> {
            value.set(enabled);
            value.save();
        }).defaultValue(value.getDefault()).tooltip(Component.translatable("spicedcider.options." + key + ".tooltip")));
    }
}
