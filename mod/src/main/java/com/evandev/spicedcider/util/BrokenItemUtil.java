package com.evandev.spicedcider.util;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

public final class BrokenItemUtil {

    private BrokenItemUtil() {
    }

    public static boolean isBroken(ItemStack stack) {
        if (!SpicedCiderConfig.COMMON.keepBrokenItems.get()) return false;

        int maxDamage = stack.getMaxDamage() - (stack.getItem() instanceof ElytraItem ? 1 : 0);
        return stack.isDamageableItem() && stack.getDamageValue() > 0 && stack.getDamageValue() >= maxDamage;
    }

    public static boolean isKeeper(ItemStack stack) {
        return stack.is(ModTags.Items.KEPT_WHEN_BROKEN) && !stack.is(ModTags.Items.DESTROYED_WHEN_BROKEN);
    }
}
