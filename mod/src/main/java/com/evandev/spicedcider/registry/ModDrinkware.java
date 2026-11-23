package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.bountifulfares.BountifulFaresCompat;
import com.evandev.spicedcider.compat.bountifulfares.LavenderTeaMugItem;
import com.evandev.spicedcider.compat.culturaldelights.CiderDrinkItems;
import com.evandev.spicedcider.content.item.CeramicMugItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public class ModDrinkware {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpicedCider.MOD_ID);

    public static final DeferredItem<Item> CERAMIC_MUG = ITEMS.register("ceramic_mug",
            () -> new CeramicMugItem(new Item.Properties()));

    @Nullable
    public static final DeferredItem<Item> LAVENDER_TEA_MUG = BountifulFaresCompat.isLoaded()
            ? ITEMS.register("lavender_tea_mug", LavenderTeaMugItem::new)
            : null;

    @Nullable
    public static final DeferredItem<Item> PLUM_CIDER = ciderDrinksLoaded()
            ? ITEMS.register("plum_cider", CiderDrinkItems::cider)
            : null;

    @Nullable
    public static final DeferredItem<Item> HOARY_CIDER = ciderDrinksLoaded()
            ? ITEMS.register("hoary_cider", CiderDrinkItems::cider)
            : null;

    private static boolean ciderDrinksLoaded() {
        ModList mods = ModList.get();
        return BountifulFaresCompat.isLoaded() && mods.isLoaded("culturaldelights") && mods.isLoaded("farmersdelight");
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(ModDrinkware::buildContents);
    }

    private static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(new ItemStack(Items.GLASS_BOTTLE), new ItemStack(CERAMIC_MUG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
