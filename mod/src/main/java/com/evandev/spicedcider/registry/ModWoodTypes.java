package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWoodTypes {
    public static final WoodType TWISTED = WoodType.register(new WoodType(SpicedCider.MOD_ID + ":twisted", BlockSetType.OAK));
}
