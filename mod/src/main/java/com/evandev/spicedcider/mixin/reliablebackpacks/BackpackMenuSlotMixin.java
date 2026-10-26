package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("reliable_backpacks")
@Mixin(targets = "com.evandev.reliable_backpacks.common.menus.BackpackMenu$1", remap = false)
public class BackpackMenuSlotMixin {
    @WrapMethod(method = "mayPlace")
    private boolean spicedcider$allowAnything(ItemStack stack, Operation<Boolean> original) {
        return true;
    }
}
