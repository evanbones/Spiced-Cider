package com.evandev.spicedcider.compat.quark;

import net.minecraft.world.item.ItemStack;
import org.violetmoon.quark.content.tools.base.RuneColor;
import org.violetmoon.quark.content.tools.module.ColorRunesModule;

final class QuarkRuneBridge {

    static ItemStack apply(ItemStack target, String color) {
        if (!ColorRunesModule.canHaveRune(target)) return ItemStack.EMPTY;
        RuneColor rune = RuneColor.byName(color);
        if (rune == null || ColorRunesModule.getStackColor(target) == rune) return ItemStack.EMPTY;
        ItemStack result = target.copyWithCount(1);
        ColorRunesModule.withRune(result, rune);
        return result;
    }

    static ItemStack display(ItemStack target, String color) {
        RuneColor rune = RuneColor.byName(color);
        if (rune == null) return ItemStack.EMPTY;
        return ColorRunesModule.withRune(target.copyWithCount(1), rune);
    }

    static boolean canHaveRune(ItemStack stack) {
        return ColorRunesModule.canHaveRune(stack);
    }

    static int textColor(String color) {
        RuneColor rune = RuneColor.byName(color);
        return rune == null ? 0xFFFFFF : rune.getTextColor().getValue();
    }
}
