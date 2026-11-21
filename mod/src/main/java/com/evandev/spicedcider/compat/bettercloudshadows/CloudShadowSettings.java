package com.evandev.spicedcider.compat.bettercloudshadows;

import com.evandev.better_cloud_shadows.config.ModConfig;
import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.client.options.setting.Setting;
import com.evandev.spicedcider.client.options.setting.StepSlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class CloudShadowSettings {
    private static final int DEFAULT_STRENGTH = 100;

    public static Entry enabled() {
        return new SettingEntry(CycleSetting.toggle(Component.translatable("spicedcider.options.cloud_shadows"),
                        () -> ModConfig.get().cloudShadows,
                        enabled -> {
                            ModConfig.get().cloudShadows = enabled;
                            ModConfig.save();
                        })
                .defaultValue(true)
                .tooltip(Component.translatable("spicedcider.options.cloud_shadows.tooltip")));
    }

    public static Entry strength() {
        return new SettingEntry(new Strength());
    }

    private static int percent() {
        return (int) Math.round(ModConfig.get().cloudShadowStrength * 100.0);
    }

    private static void setPercent(int percent) {
        ModConfig.get().cloudShadowStrength = percent / 100.0;
    }

    private static final class Strength implements Setting {

        @Override
        public Component name() {
            return Component.translatable("spicedcider.options.cloud_shadow_strength");
        }

        @Override
        public List<FormattedCharSequence> tooltip() {
            return Minecraft.getInstance().font.split(Component.translatable("spicedcider.options.cloud_shadow_strength.tooltip"), 200);
        }

        @Override
        public boolean isDefault() {
            return percent() == DEFAULT_STRENGTH;
        }

        @Override
        public boolean canReset() {
            return true;
        }

        @Override
        public void reset() {
            setPercent(DEFAULT_STRENGTH);
            ModConfig.save();
        }

        @Override
        public boolean isActive() {
            return ModConfig.get().cloudShadows;
        }

        @Override
        public AbstractWidget createWidget(int width) {
            return new StepSlider(width, 0, (int) Math.round(ModConfig.MAX_STRENGTH * 100.0), 5, percent(),
                    value -> Component.translatable("spicedcider.options.value.percent", value),
                    CloudShadowSettings::setPercent,
                    ModConfig::save);
        }
    }
}
