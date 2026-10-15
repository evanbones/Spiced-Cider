package com.evandev.spicedcider.compat.reliableremover;

import com.evandev.reliable_remover.api.ReliableRemoverAPI;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class ReliableRemoverBridge {

    private ReliableRemoverBridge() {
    }

    static boolean isRemoved(Item item) {
        return ReliableRemoverAPI.isLootBlocked(new ItemStack(item));
    }
}
