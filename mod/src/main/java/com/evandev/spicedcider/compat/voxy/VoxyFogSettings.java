package com.evandev.spicedcider.compat.voxy;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.Setting;
import com.evandev.spicedcider.client.options.setting.StepSlider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class VoxyFogSettings {

    public static Entry start() {
        return new SettingEntry(new Percent("fog_start", SpicedCiderConfig.CLIENT.voxyFogStart, -100));
    }

    public static Entry end() {
        return new SettingEntry(new Percent("fog_end", SpicedCiderConfig.CLIENT.voxyFogEnd, 1));
    }

    private record Percent(String key, ModConfigSpec.IntValue value, int min) implements Setting {

        @Override
        public Component name() {
            return Component.translatable("spicedcider.options." + key);
        }

        @Override
        public List<FormattedCharSequence> tooltip() {
            return Minecraft.getInstance().font.split(Component.translatable("spicedcider.options." + key + ".tooltip"), 200);
        }

        @Override
        public boolean isDefault() {
            return value.get().equals(value.getDefault());
        }

        @Override
        public boolean canReset() {
            return true;
        }

        @Override
        public void reset() {
            value.set(value.getDefault());
            value.save();
        }

        @Override
        public boolean isActive() {
            return SpicedCiderConfig.CLIENT.voxyFogOverride.get();
        }

        @Override
        public AbstractWidget createWidget(int width) {
            return new StepSlider(width, min, 100, 1, value.get(),
                    percent -> Component.translatable("spicedcider.options.value.percent", percent),
                    value::set,
                    value::save);
        }
    }
}
