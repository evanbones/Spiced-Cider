package com.evandev.spicedcider.compat.supplementaries;

import net.mehvahdjukaar.supplementaries.common.block.blocks.StickBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class PrismarineRodBlock extends StickBlock {

    public PrismarineRodBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Item asItem() {
        return BuiltInRegistries.ITEM.get(PrismarineRods.ROD_ITEM);
    }
}
