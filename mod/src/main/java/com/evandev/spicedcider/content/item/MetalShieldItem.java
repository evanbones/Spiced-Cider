package com.evandev.spicedcider.content.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.NotNull;

public class MetalShieldItem extends ShieldItem {
    private final TagKey<Item> repairTag;

    public MetalShieldItem(TagKey<Item> repairTag, Properties properties) {
        super(properties);
        this.repairTag = repairTag;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        return repair.is(this.repairTag);
    }
}
