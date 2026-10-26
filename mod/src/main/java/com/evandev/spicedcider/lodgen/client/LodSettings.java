package com.evandev.spicedcider.lodgen.client;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.client.options.setting.Setting;
import com.evandev.spicedcider.client.options.setting.StepSlider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.lodgen.LodGen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class LodSettings {

    public static Entry generate() {
        ModConfigSpec.BooleanValue value = SpicedCiderConfig.COMMON.lodGeneration;
        return new SettingEntry(CycleSetting.toggle(Component.translatable("option.spicedcider.lodGeneration"), value::get, enabled -> {
            value.set(enabled);
            value.save();
        }).defaultValue(value.getDefault()).tooltip(Component.translatable("option.spicedcider.lodGeneration.tooltip")));
    }

    public static Entry threads() {
        return new SettingEntry(new Threads(SpicedCiderConfig.COMMON.lodGenerationThreads));
    }

    private record Threads(ModConfigSpec.IntValue value) implements Setting {

        @Override
        public Component name() {
            return Component.translatable("option.spicedcider.lodGenerationThreads");
        }

        @Override
        public List<FormattedCharSequence> tooltip() {
            return Minecraft.getInstance().font.split(Component.translatable("option.spicedcider.lodGenerationThreads.tooltip"), 200);
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
            return SpicedCiderConfig.COMMON.lodGeneration.get();
        }

        @Override
        public AbstractWidget createWidget(int width) {
            return new StepSlider(width, 0, 8, 1, value.get(),
                    threads -> threads == 0
                            ? Component.translatable("spicedcider.options.value.auto_threads", LodGen.autoThreads())
                            : Component.translatable("spicedcider.options.value.threads", threads),
                    value::set,
                    value::save);
        }
    }
}
