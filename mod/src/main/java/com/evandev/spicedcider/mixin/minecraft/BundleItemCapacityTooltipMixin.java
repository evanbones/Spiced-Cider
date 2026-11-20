package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.util.BundleCapacity;
import net.minecraft.world.item.BundleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BundleItem.class)
public class BundleItemCapacityTooltipMixin {
    @ModifyConstant(method = "appendHoverText", constant = @Constant(intValue = 64))
    private int spicedcider$showCapacity(int vanilla) {
        return BundleCapacity.get();
    }
}
