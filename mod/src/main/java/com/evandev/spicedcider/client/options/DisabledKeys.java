package com.evandev.spicedcider.client.options;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.settings.KeyModifier;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class DisabledKeys {
    private static boolean applied;

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (applied) return;
        applied = true;

        Minecraft minecraft = Minecraft.getInstance();
        boolean changed = false;
        for (KeyMapping mapping : minecraft.options.keyMappings) {
            if (!KeyLayout.isDisabled(mapping.getName())) continue;
            if (mapping.isUnbound() && mapping.getKeyModifier() == KeyModifier.NONE) continue;
            mapping.setKeyModifierAndCode(KeyModifier.NONE, InputConstants.UNKNOWN);
            changed = true;
        }
        if (!changed) return;
        KeyMapping.resetMapping();
        minecraft.options.save();
    }
}
