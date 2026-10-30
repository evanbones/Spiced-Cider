package com.evandev.spicedcider.compat.quark;

import com.evandev.spicedcider.registry.ModDataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.List;

public final class QuarkRunes {

    public static final ResourceLocation RUNE = ResourceLocation.fromNamespaceAndPath("quark", "smithing_template_rune");
    public static final String BLANK = "blank";
    public static final String RAINBOW = "rainbow";
    public static final List<String> COLORS = List.of(
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black", RAINBOW, BLANK);

    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded("quark");
        }
        return loaded;
    }

    public static boolean isRune(ItemStack stack) {
        return isLoaded() && RUNE.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static String color(ItemStack rune) {
        return rune.getOrDefault(ModDataComponents.RUNE_COLOR.get(), BLANK);
    }

    public static ItemStack rune(String color) {
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(RUNE));
        if (!BLANK.equals(color)) {
            stack.set(ModDataComponents.RUNE_COLOR.get(), color);
        }
        return stack;
    }

    public static ItemStack apply(ItemStack target, ItemStack rune) {
        return QuarkRuneBridge.apply(target, color(rune));
    }

    public static ItemStack display(ItemStack target, String color) {
        return QuarkRuneBridge.display(target, color);
    }

    public static boolean canHaveRune(ItemStack stack) {
        return QuarkRuneBridge.canHaveRune(stack);
    }

    public static int textColor(String color) {
        return QuarkRuneBridge.textColor(color);
    }
}
