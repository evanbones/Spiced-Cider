package com.evandev.spicedcider.compat.vanillabackport;

import com.blackgear.vanillabackport.common.level.items.spear.SpearItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

final class VanillaBackportBridge {

    static Item createLance(Tier tier, Item.Properties properties) {
        return new SpearItem(tier, 1.0F, 1.25F, 0.4F, 2.75F, 7.0F, 6.0F, 5.1F, 9.5F, 4.6F, properties);
    }
}
