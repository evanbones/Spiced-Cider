package com.evandev.spicedcider.compat.iteminteractions;

import com.evandev.spicedcider.SpicedCider;
import fuzs.iteminteractions.api.v1.provider.ItemContentsProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemInteractionsCompat {
    private static final DeferredRegister<ItemContentsProvider.Type> TYPES = DeferredRegister.create(ItemContentsProvider.REGISTRY_KEY, SpicedCider.MOD_ID);

    public static final DeferredHolder<ItemContentsProvider.Type, ItemContentsProvider.Type> POCKET_JUKEBOX = TYPES.register("pocket_jukebox",
            () -> new ItemContentsProvider.Type(PocketJukeboxProvider.CODEC));

    public static void register(IEventBus modEventBus) {
        TYPES.register(modEventBus);
    }
}
