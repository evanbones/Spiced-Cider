package com.evandev.spicedcider.compat.toilandtrouble;

import net.minecraft.world.item.ItemStack;

public interface AutomatedCauldron {

    boolean spicedcider$accepts(ItemStack stack);

    int spicedcider$drained();

    void spicedcider$setDrained(int drained);
}
