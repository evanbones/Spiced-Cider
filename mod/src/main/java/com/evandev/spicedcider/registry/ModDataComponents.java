package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAP_HEIGHT = DATA_COMPONENTS.registerComponentType(
            "map_height",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
}
