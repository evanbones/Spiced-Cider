package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMapDecorationTypes {
    public static final DeferredRegister<MapDecorationType> MAP_DECORATION_TYPES = DeferredRegister.create(BuiltInRegistries.MAP_DECORATION_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<MapDecorationType, MapDecorationType> RUINED_PORTAL = MAP_DECORATION_TYPES.register("ruined_portal",
            () -> new MapDecorationType(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "ruined_portal"), true, MapColor.COLOR_PURPLE.col, true, false));
}
