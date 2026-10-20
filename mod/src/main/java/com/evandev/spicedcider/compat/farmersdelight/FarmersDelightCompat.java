package com.evandev.spicedcider.compat.farmersdelight;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

public final class FarmersDelightCompat {

    public static final String MOD_ID = "farmersdelight";
    private static final TagKey<Block> MINEABLE_WITH_KNIFE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(MOD_ID, "mineable/knife"));

    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MOD_ID);
        }
        return loaded;
    }

    public static Item createKnife(Tier tier, Item.Properties properties) {
        return isLoaded()
                ? FarmersDelightBridge.createKnife(tier, properties)
                : new DiggerItem(tier, MINEABLE_WITH_KNIFE, properties.attributes(DiggerItem.createAttributes(tier, 0.5F, -2.0F)));
    }
}
