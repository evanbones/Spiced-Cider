package com.evandev.spicedcider.compat.reliableremover;

import com.evandev.reliable_remover.api.ReliableRemoverAPI;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class ReliableRemoverBridge {

    static boolean isRemoved(Item item) {
        return ReliableRemoverAPI.isLootBlocked(new ItemStack(item));
    }

    static boolean isHiddenFromViewers(Item item) {
        ItemStack stack = new ItemStack(item);
        return ReliableRemoverAPI.isLootBlocked(stack) || ReliableRemoverAPI.isCreativeBlocked(stack) || ReliableRemoverAPI.isItemHidden(stack);
    }
}
