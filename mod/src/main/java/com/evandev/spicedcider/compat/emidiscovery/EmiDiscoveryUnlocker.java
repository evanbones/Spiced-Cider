package com.evandev.spicedcider.compat.emidiscovery;

import net.funkpla.emi_discovery.KnownItems;
import net.minecraft.world.item.Item;

import java.util.Collection;

public final class EmiDiscoveryUnlocker {

    public static void unlock(Collection<Item> items) {
        KnownItems.addKnownItems(items);
    }
}
