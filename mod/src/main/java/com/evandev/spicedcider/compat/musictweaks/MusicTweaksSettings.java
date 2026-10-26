package com.evandev.spicedcider.compat.musictweaks;

import com.evandev.spicedcider.client.options.SettingsScreen;
import com.evandev.spicedcider.client.options.entry.CategoryEntry;
import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.client.options.setting.Setting;
import com.evandev.spicedcider.client.options.setting.StepSlider;
import com.evandev.spicedcider.client.options.setting.VanillaSetting;
import com.blackgear.vanillabackport.client.api.modules.options.OptionsAccess;
import com.evandev.music_tweaks.config.ModConfig;
import com.evandev.music_tweaks.config.MusicFrequency;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MusicTweaksSettings {

    private static final ModConfig DEFAULTS = new ModConfig();

    public static Entry category(SettingsScreen screen) {
        ModConfig config = ModConfig.get();
        List<Entry> list = new ArrayList<>();
        list.add(ModList.get().isLoaded("vanillabackport") ? backportFrequency() : new SettingEntry(new CycleSetting<>(
                Component.translatable("config.music_tweaks.music_frequency"),
                () -> List.of(MusicFrequency.values()),
                () -> config.musicFrequency,
                value -> {
                    config.musicFrequency = value;
                    ModConfig.save();
                },
                MusicFrequency::getDisplayName
        ).defaultValue(DEFAULTS.musicFrequency).tooltip(Component.translatable("config.music_tweaks.music_frequency.tooltip"))));
        list.add(toggle("show_music_toast", () -> config.showMusicToast, v -> config.showMusicToast = v, DEFAULTS.showMusicToast));
        list.add(toggle("play_toast_sound", () -> config.playToastSound, v -> config.playToastSound = v, DEFAULTS.playToastSound));
        list.add(toggle("play_music_when_paused", () -> config.playMusicWhenPaused, v -> config.playMusicWhenPaused = v, DEFAULTS.playMusicWhenPaused));
        list.add(toggle("mono_jukeboxes", () -> config.monoJukeboxes, v -> config.monoJukeboxes = v, DEFAULTS.monoJukeboxes));
        list.add(toggle("jukebox_fade_out", () -> config.jukeboxFadeOut, v -> config.jukeboxFadeOut = v, DEFAULTS.jukeboxFadeOut));
        list.add(new SettingEntry(new JukeboxDistance(config)));
        return new CategoryEntry(Component.translatable("spicedcider.options.category.music"), list, screen.collapsed());
    }

    private static Entry backportFrequency() {
        return new SettingEntry(new VanillaSetting<>(((OptionsAccess) Minecraft.getInstance().options).musicFrequency()));
    }

    private static Entry toggle(String key, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean defaultValue) {
        return new SettingEntry(CycleSetting.toggle(Component.translatable("config.music_tweaks." + key), getter, value -> {
            setter.accept(value);
            ModConfig.save();
        }).defaultValue(defaultValue).tooltip(Component.translatable("config.music_tweaks." + key + ".tooltip")));
    }

    private record JukeboxDistance(ModConfig config) implements Setting {

        @Override
        public Component name() {
            return Component.translatable("config.music_tweaks.jukebox_distance");
        }

        @Override
        public List<FormattedCharSequence> tooltip() {
            return Minecraft.getInstance().font.split(Component.translatable("config.music_tweaks.jukebox_distance.tooltip"), 200);
        }

        @Override
        public boolean isDefault() {
            return (int) config.jukeboxDistance == (int) DEFAULTS.jukeboxDistance;
        }

        @Override
        public boolean canReset() {
            return true;
        }

        @Override
        public void reset() {
            config.jukeboxDistance = DEFAULTS.jukeboxDistance;
            ModConfig.save();
        }

        @Override
        public boolean isActive() {
            return config.monoJukeboxes;
        }

        @Override
        public AbstractWidget createWidget(int width) {
            return new StepSlider(width, 8, 256, 8, (int) config.jukeboxDistance,
                    value -> Component.translatable("spicedcider.options.value.blocks", value),
                    value -> config.jukeboxDistance = value,
                    ModConfig::save);
        }
    }
}
