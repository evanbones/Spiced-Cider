package com.evandev.spicedcider.compat.tide;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.List;

public final class TideCompat {

    public static final String MOD_ID = "tide";

    private static final String DEFAULT_LINE_COLOR = "#FFFFFF";

    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MOD_ID);
        }
        return loaded;
    }

    public static Item createHookItem(Item.Properties properties, String descriptionKey) {
        return TideBridge.createHookItem(properties, descriptionKey);
    }

    public static FishingRodItem createFishingRod(int baitSlots, double durability, Item.Properties properties) {
        return TideBridge.createFishingRod(baitSlots, durability, properties);
    }

    public static void registerFishingMediums() {
        if (isLoaded() && ModList.get().isLoaded("dimensional_tears")) {
            TideBridge.registerDimensionalTearsMedium();
        }
    }

    public static ItemStack getLine(ItemStack rod) {
        return isLoaded() ? TideBridge.getLine(rod) : ItemStack.EMPTY;
    }

    public static ItemStack getBobber(ItemStack rod) {
        return isLoaded() ? TideBridge.getBobber(rod) : ItemStack.EMPTY;
    }

    public static String getLineColor(ItemStack line) {
        return isLoaded() ? TideBridge.getLineColor(line) : DEFAULT_LINE_COLOR;
    }

    public static void stripBucketableMarker(ItemStack stack) {
        if (isLoaded() && !stack.isEmpty()) TideBridge.stripBucketableMarker(stack);
    }

    public static boolean isBait(ItemStack stack) {
        return isLoaded() && TideBridge.isBait(stack);
    }

    public static int baitLuck(ItemStack stack) {
        return isLoaded() ? TideBridge.baitLuck(stack) : 0;
    }

    public static int baitSpeed(ItemStack stack) {
        return isLoaded() ? TideBridge.baitSpeed(stack) : 0;
    }

    public static List<ItemStack> catchFish(ServerLevel level, BlockPos pos, int luck) {
        return TideBridge.catchFish(level, pos, luck);
    }
}
