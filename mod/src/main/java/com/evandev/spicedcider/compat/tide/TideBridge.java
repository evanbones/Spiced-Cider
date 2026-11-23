package com.evandev.spicedcider.compat.tide;

import com.li64.tide.compat.seasons.SeasonsCompat;
import com.li64.tide.data.TideData;
import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.mediums.FishingMedium;
import com.li64.tide.data.fishing.selector.FishingRandomSelector;
import com.li64.tide.util.BaitUtils;
import com.li64.tide.util.TideUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import com.li64.tide.data.item.TideDataComponents;
import com.li64.tide.data.rods.CustomRodManager;
import com.li64.tide.registries.items.FishingHookItem;
import com.li64.tide.registries.items.FishingLineItem;
import com.li64.tide.registries.items.TideFishingRodItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

final class TideBridge {

    static Item createHookItem(Item.Properties properties, String descriptionKey) {
        return new FishingHookItem(properties, descriptionKey);
    }

    static FishingRodItem createFishingRod(int baitSlots, double durability, Item.Properties properties) {
        return new TideFishingRodItem(baitSlots, durability, properties);
    }

    static void registerDimensionalTearsMedium() {
        if (FishingMedium.MEDIUMS.stream().noneMatch(medium -> medium instanceof DimensionalTearsMedium)) {
            FishingMedium.register(new DimensionalTearsMedium());
        }
    }

    static ItemStack getLine(ItemStack rod) {
        return CustomRodManager.getLine(rod);
    }

    static ItemStack getBobber(ItemStack rod) {
        return CustomRodManager.getBobber(rod);
    }

    static String getLineColor(ItemStack line) {
        return FishingLineItem.getColor(line);
    }

    static void stripBucketableMarker(ItemStack stack) {
        stack.remove(TideDataComponents.IS_BUCKETABLE);
    }

    static boolean isBait(ItemStack stack) {
        return BaitUtils.isBait(stack);
    }

    static int baitSpeed(ItemStack stack) {
        return BaitUtils.getBaitSpeed(stack);
    }

    static int baitLuck(ItemStack stack) {
        return BaitUtils.getBaitLuck(stack);
    }

    static List<ItemStack> catchFish(ServerLevel level, BlockPos pos, int luck) {
        Holder<Biome> biome = level.getBiome(pos);
        FishingContext context = new FishingContext(level, null, null, level.getRandom(), pos.getCenter(), pos, luck,
                FishingMedium.WATER.id().getPath(), biome, TideUtils.findClosestNonWaterBiome(level, pos, 12, 2).orElse(biome),
                level.dimension(), TideUtils.getTemperatureAt(pos, level), level.getMoonPhase(), SeasonsCompat.getSeason(level));
        return FishingRandomSelector.select(TideData.FISH.get().values(), context).items();
    }
}
