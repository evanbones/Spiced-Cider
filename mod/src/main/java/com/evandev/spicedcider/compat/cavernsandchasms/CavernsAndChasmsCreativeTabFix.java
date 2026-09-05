package com.evandev.spicedcider.compat.cavernsandchasms;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public final class CavernsAndChasmsCreativeTabFix {

    private static final ResourceLocation SUGILITE = ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "sugilite");
    private static final ResourceLocation CYLINDRITE = ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "cylindrite");

    @SubscribeEvent
    public static void addMissingCylindrite(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.NATURAL_BLOCKS) return;

        Item sugilite = BuiltInRegistries.ITEM.get(SUGILITE);
        Item cylindrite = BuiltInRegistries.ITEM.get(CYLINDRITE);
        if (sugilite == Items.AIR || cylindrite == Items.AIR) return;

        ItemStack stack = new ItemStack(cylindrite);
        if (event.getParentEntries().contains(stack) || event.getSearchEntries().contains(stack)) return;

        ItemStack anchor = new ItemStack(sugilite);
        if (event.getParentEntries().contains(anchor) && event.getSearchEntries().contains(anchor)) {
            event.insertAfter(anchor, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        } else {
            event.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
