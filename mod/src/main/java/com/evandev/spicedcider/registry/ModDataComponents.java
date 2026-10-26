package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.item.PocketJukeboxContents;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAP_HEIGHT = DATA_COMPONENTS.registerComponentType(
            "map_height",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORK_NOTE = DATA_COMPONENTS.registerComponentType(
            "fork_note",
            builder -> builder.persistent(Codec.intRange(0, 24)).networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PocketJukeboxContents>> POCKET_JUKEBOX = DATA_COMPONENTS.registerComponentType(
            "pocket_jukebox",
            builder -> builder.persistent(PocketJukeboxContents.CODEC).networkSynchronized(PocketJukeboxContents.STREAM_CODEC).cacheEncoding()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> FORK_CONDUCTING = DATA_COMPONENTS.registerComponentType(
            "fork_conducting",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );
}
