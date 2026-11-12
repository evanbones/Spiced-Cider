package com.evandev.spicedcider.mixin.quark;

import com.evandev.spicedcider.util.BrokenItemUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.violetmoon.quark.content.tweaks.module.HoeHarvestingModule;

@IfModLoaded("quark")
@Mixin(value = HoeHarvestingModule.class, remap = false)
public class HoeHarvestingBrokenToolMixin {

    @WrapMethod(method = "isHoe")
    private static boolean spicedcider$brokenHoeNoHarvest(ItemStack stack, Operation<Boolean> original) {
        return !BrokenItemUtil.isBroken(stack) && original.call(stack);
    }
}
