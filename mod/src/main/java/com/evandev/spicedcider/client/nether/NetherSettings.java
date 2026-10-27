package com.evandev.spicedcider.client.nether;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.client.options.setting.Setting;
import com.evandev.spicedcider.client.options.setting.StepSlider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class NetherSettings {

    public static Entry heatHaze() {
        return toggle("heat_haze", SpicedCiderConfig.CLIENT.netherHeatHaze);
    }

    public static Entry mist() {
        return toggle("nether_mist", SpicedCiderConfig.CLIENT.netherMist);
    }

    public static Entry ambientTint() {
        return new SettingEntry(new AmbientTint(SpicedCiderConfig.CLIENT.netherAmbientTint));
    }

    private static Entry toggle(String key, ModConfigSpec.BooleanValue value) {
        return new SettingEntry(CycleSetting.toggle(Component.translatable("spicedcider.options." + key), value::get, enabled -> {
            value.set(enabled);
            value.save();
        }).defaultValue(value.getDefault()).tooltip(Component.translatable("spicedcider.options." + key + ".tooltip")));
    }

    private record AmbientTint(ModConfigSpec.IntValue value) implements Setting {

        @Override
        public Component name() {
            return Component.translatable("spicedcider.options.nether_tint");
        }

        @Override
        public List<FormattedCharSequence> tooltip() {
            return Minecraft.getInstance().font.split(Component.translatable("spicedcider.options.nether_tint.tooltip"), 200);
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
            return true;
        }

        @Override
        public AbstractWidget createWidget(int width) {
            return new StepSlider(width, 0, 100, 1, value.get(),
                    percent -> Component.translatable("spicedcider.options.value.percent", percent),
                    value::set,
                    value::save);
        }
    }
}
