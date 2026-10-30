package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.quark.QuarkRunes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class RuneClientEvents {

    private static final int BLANK = 0xFF8C8C8C;

    @SubscribeEvent
    public static void showRuneColor(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!QuarkRunes.isRune(stack)) return;
        String color = QuarkRunes.color(stack);
        event.getToolTip().add(1, Component.translatable("rune.quark." + color).withColor(QuarkRunes.textColor(color)));
    }

    public static void registerColors(RegisterColorHandlersEvent.Item event) {
        if (!QuarkRunes.isLoaded()) return;
        Item rune = BuiltInRegistries.ITEM.get(QuarkRunes.RUNE);
        if (rune == Items.AIR) return;
        event.register((stack, tintIndex) -> tintIndex == 1 ? tint(QuarkRunes.color(stack)) : -1, rune);
    }

    private static int tint(String color) {
        if (QuarkRunes.RAINBOW.equals(color)) {
            float hue = (System.currentTimeMillis() % 4000L) / 4000.0F;
            return 0xFF000000 | Mth.hsvToRgb(hue, 0.6F, 1.0F);
        }
        DyeColor dye = DyeColor.byName(color, null);
        return dye == null ? BLANK : 0xFF000000 | dye.getTextureDiffuseColor();
    }
}
