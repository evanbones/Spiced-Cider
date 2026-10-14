package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.architectspalette.ArchitectsPaletteCompat;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.Supplier;

public class ModBoatTypes {
    @SuppressWarnings("unused")
    public static final EnumProxy<Boat.Type> TWISTED = new EnumProxy<>(Boat.Type.class,
            (Supplier<Block>) ArchitectsPaletteCompat::twistedPlanks,
            SpicedCider.MOD_ID + ":twisted",
            (Supplier<Item>) () -> ModItems.TWISTED_BOAT != null ? ModItems.TWISTED_BOAT.get() : Items.AIR,
            (Supplier<Item>) () -> ModItems.TWISTED_CHEST_BOAT != null ? ModItems.TWISTED_CHEST_BOAT.get() : Items.AIR,
            (Supplier<Item>) () -> Items.STICK,
            false);
}
