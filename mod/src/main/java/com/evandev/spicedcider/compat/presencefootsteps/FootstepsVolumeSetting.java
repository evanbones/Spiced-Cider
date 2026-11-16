package com.evandev.spicedcider.compat.presencefootsteps;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.Setting;
import com.evandev.spicedcider.client.options.setting.StepSlider;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.ha3.presencefootsteps.PFConfig;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLPaths;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FootstepsVolumeSetting implements Setting {
    private static final int MOD_DEFAULT = 70;
    private static Integer packDefault;

    public static Entry create() {
        return new SettingEntry(new FootstepsVolumeSetting());
    }

    private static PFConfig config() {
        return PresenceFootsteps.getInstance().config;
    }

    private static int packDefault() {
        if (packDefault == null) {
            packDefault = MOD_DEFAULT;
            Path file = FMLPaths.CONFIGDIR.get().resolve("yosbr/config/presencefootsteps/userconfig.json");
            if (Files.isRegularFile(file)) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    if (json.has("volume")) {
                        packDefault = json.get("volume").getAsInt();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return packDefault;
    }

    @Override
    public Component name() {
        return Component.translatable("spicedcider.options.footsteps_volume");
    }

    @Override
    public boolean isDefault() {
        return config().getGlobalVolume() == packDefault();
    }

    @Override
    public boolean canReset() {
        return true;
    }

    @Override
    public void reset() {
        config().setGlobalVolume(packDefault());
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public AbstractWidget createWidget(int width) {
        return new StepSlider(width, 0, 100, 1, config().getGlobalVolume(),
                percent -> percent == 0 ? Component.translatable("options.off") : Component.translatable("spicedcider.options.value.percent", percent),
                volume -> config().setGlobalVolume(volume),
                () -> {
                });
    }
}
