package com.evandev.spicedcider.compat.polytone;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.mixin.polytone.ConfigsManagerAccessor;
import net.mehvahdjukaar.polytone.Polytone;
import net.mehvahdjukaar.polytone.content.config.OptionHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class GodraysSettings {
    private static final ResourceLocation ENABLED = ResourceLocation.fromNamespaceAndPath("sunbathing", "enabled");

    @Nullable
    public static Entry godrays() {
        if (holder() == null) {
            return null;
        }
        return new SettingEntry(CycleSetting.toggle(Component.translatable("spicedcider.options.godrays"), GodraysSettings::enabled, GodraysSettings::setEnabled)
                .defaultValue(true)
                .tooltip(Component.translatable("spicedcider.options.godrays.tooltip")));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static OptionHolder<Boolean> holder() {
        for (OptionHolder<?> holder : ((ConfigsManagerAccessor) Polytone.CONFIGS).spicedcider$shownOptions()) {
            if (ENABLED.equals(holder.fileId) && holder.get() instanceof Boolean) {
                return (OptionHolder<Boolean>) holder;
            }
        }
        return null;
    }

    private static boolean enabled() {
        OptionHolder<Boolean> holder = holder();
        return holder == null || holder.get();
    }

    private static void setEnabled(boolean enabled) {
        OptionHolder<Boolean> holder = holder();
        if (holder == null) {
            return;
        }
        holder.option.set(enabled);
        ((ConfigsManagerAccessor) Polytone.CONFIGS).spicedcider$saveConfigsToDisk(List.of(holder));
    }
}
