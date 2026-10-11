package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public final class CaveMapHeights {
    public static final int TERRAIN = Integer.MIN_VALUE;

    private CaveMapHeights() {
    }

    public record FixedHeight(String dimension, int height) {
    }

    public static FixedHeight parseFixed(String entry) {
        int split = entry.lastIndexOf('=');
        if (split <= 0 || ResourceLocation.tryParse(entry.substring(0, split)) == null) return null;
        try {
            return new FixedHeight(entry.substring(0, split), Integer.parseInt(entry.substring(split + 1).trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int heightFor(Level level, Entity viewer, MapItemSavedData data) {
        SpicedCiderConfig.Common config = SpicedCiderConfig.COMMON;
        if (!config.caveMaps.get()) return TERRAIN;
        String dimension = level.dimension().location().toString();
        for (String entry : config.caveMapsFixedHeights.get()) {
            FixedHeight fixed = parseFixed(entry);
            if (fixed != null && fixed.dimension().equals(dimension)) return fixed.height();
        }
        boolean ceiling = level.dimensionType().hasCeiling();
        if (config.caveMapsCreationHeightDimensions.get().contains(dimension) || ceiling && config.caveMapsUseCreationHeight.get()) {
            Integer created = creationHeight(level, viewer, data);
            if (created != null) return created;
        }
        return ceiling ? config.caveMapsFixedHeight.get() : TERRAIN;
    }

    private static Integer creationHeight(Level level, Entity viewer, MapItemSavedData data) {
        if (!(viewer instanceof Player player)) return null;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.getItem() instanceof MapItem && MapItem.getSavedData(stack, level) == data) {
                return stack.get(ModDataComponents.MAP_HEIGHT);
            }
        }
        return null;
    }
}
