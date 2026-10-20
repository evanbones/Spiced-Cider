package com.evandev.spicedcider.compat.farmersdelight;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import vectorwing.farmersdelight.common.item.KnifeItem;

final class FarmersDelightBridge {

    static Item createKnife(Tier tier, Item.Properties properties) {
        return new KnifeItem(tier, properties.attributes(KnifeItem.createAttributes(tier, 0.5F, -2.0F)));
    }
}
