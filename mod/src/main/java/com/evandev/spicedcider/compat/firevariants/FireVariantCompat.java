package com.evandev.spicedcider.compat.firevariants;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;

public class FireVariantCompat {
    public static boolean supplementaries;
    public static boolean suppSquared;
    public static boolean buzzierBees;
    public static boolean caverns;
    public static boolean autumnity;
    public static boolean environmental;
    public static boolean glowlight;

    public static void register() {
        ModList mods = ModList.get();
        supplementaries = mods.isLoaded("supplementaries");
        suppSquared = supplementaries && mods.isLoaded("suppsquared");
        buzzierBees = mods.isLoaded("buzzier_bees");
        caverns = mods.isLoaded("caverns_and_chasms");
        autumnity = mods.isLoaded("autumnity");
        environmental = mods.isLoaded("environmental");
        glowlight = mods.isLoaded("infernalexp");
        if (buzzierBees) BuzzierBeesFireBlocks.register(glowlight);
        if (supplementaries) SupplementariesFireBlocks.register(buzzierBees, glowlight);
        if (suppSquared && buzzierBees) SuppSquaredFireBlocks.register(glowlight);
        if (caverns) CavernsFireBlocks.register();
        if (autumnity) AutumnityFireBlocks.register();
        if (environmental) EnvironmentalFireBlocks.register();
    }

    public static void addToTabs(BuildCreativeModeTabContentsEvent event, CreativeModeTab.TabVisibility vis) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            if (supplementaries) {
                accept(event, SupplementariesFireBlocks.sulfurSconceItem, vis);
                SupplementariesFireBlocks.CANDLE_HOLDER_ITEMS.forEach(item -> accept(event, item, vis));
            }
            if (caverns) accept(event, CavernsFireBlocks.sulfurBrazierItem, vis);
            if (buzzierBees) {
                accept(event, BuzzierBeesFireBlocks.sulfurCandleItem, vis);
                accept(event, BuzzierBeesFireBlocks.glowlightCandleItem, vis);
            }
        }
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS && autumnity) {
            accept(event, AutumnityFireBlocks.largeSulfurSliceItem, vis);
        }
    }

    private static void accept(BuildCreativeModeTabContentsEvent event, DeferredItem<?> item, CreativeModeTab.TabVisibility vis) {
        if (item != null) event.accept(new ItemStack(item.get()), vis);
    }
}
