package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.common.items.BackpackItemContainer;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("reliable_backpacks")
@Mixin(value = {BackpackBlockEntity.class, BackpackItemContainer.class}, remap = false)
public class BackpackStorageFilterMixin {
    @WrapMethod(method = "canPlaceItem")
    private boolean spicedcider$allowAnything(int index, ItemStack stack, Operation<Boolean> original) {
        return true;
    }
}
