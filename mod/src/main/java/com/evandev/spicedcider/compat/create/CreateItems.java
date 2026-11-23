package com.evandev.spicedcider.compat.create;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import net.minecraft.world.item.Item;

public class CreateItems {
    public static Item unfinishedBow() {
        return new SequencedAssemblyItem(new Item.Properties().stacksTo(1));
    }
}
