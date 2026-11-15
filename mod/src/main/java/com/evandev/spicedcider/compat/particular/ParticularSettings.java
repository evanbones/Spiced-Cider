package com.evandev.spicedcider.compat.particular;

import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.leclowndu93150.particular.ParticularConfig;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class ParticularSettings {

    public static List<Entry> particles() {
        ParticularConfig.CommonConfig c = ParticularConfig.COMMON;
        return List.of(
                toggle("particles.splashes", c.waterSplash, c.lavaSplash),
                toggle("particles.waterfalls", c.cascades, c.cascadeMist, c.waterfallSpray),
                toggle("particles.ripples", c.rainRipples, c.waterDripRipples),
                toggle("particles.bubbles", c.chestBubbles, c.barrelBubbles, c.soulSandBubbles, c.poppingBubbles),
                toggle("particles.falling_leaves", c.fallingLeaves),
                toggle("particles.leaf_snow", c.leafSnow),
                toggle("particles.pollen", c.pollen),
                toggle("particles.spores", c.spores),
                toggle("particles.cave_dust", c.caveDust),
                toggle("particles.embers", c.embers),
                toggle("particles.lava_drips", c.emissiveLavaDrips),
                toggle("particles.cold_breath", c.coldBreath),
                toggle("particles.footstep_dust", c.footstepDust),
                toggle("particles.crumbs", c.cakeEatingParticles),
                toggle("particles.rainbows", c.rainbows));
    }

    public static Entry screenShake() {
        return toggle("screen_shake", ParticularConfig.COMMON.screenShake);
    }

    private static Entry toggle(String key, ModConfigSpec.BooleanValue... values) {
        ModConfigSpec.BooleanValue first = values[0];
        return new SettingEntry(CycleSetting.toggle(Component.translatable("spicedcider.options." + key), first::get, enabled -> {
            for (ModConfigSpec.BooleanValue value : values) value.set(enabled);
            ParticularConfig.COMMON_SPEC.save();
        }).defaultValue(first.getDefault()).tooltip(Component.translatable("spicedcider.options." + key + ".tooltip")));
    }
}
