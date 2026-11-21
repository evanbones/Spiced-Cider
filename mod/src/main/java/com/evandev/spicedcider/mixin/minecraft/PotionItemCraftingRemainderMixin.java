package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PotionItem.class)
public class PotionItemCraftingRemainderMixin {

    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return hasCraftingRemainingItem(stack) ? new ItemStack(Items.GLASS_BOTTLE) : ItemStack.EMPTY;
    }

    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return stack.is(Items.POTION) && SpicedCiderConfig.COMMON_SPEC.isLoaded() && SpicedCiderConfig.feature("bottleCraftingRemainder");
    }
}
