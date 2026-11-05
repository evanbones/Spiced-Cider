package com.evandev.spicedcider.compat.modestmining;

import com.baisylia.modestmining.item.custom.weapons.JavelinItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

public final class ModestMiningCompat {
    public static Item createJavelin(Tier tier, float attackDamage, float attackSpeed, float reach, Item.Properties properties) {
        return new JavelinItem(tier, attackDamage, attackSpeed, reach, properties);
    }
}
