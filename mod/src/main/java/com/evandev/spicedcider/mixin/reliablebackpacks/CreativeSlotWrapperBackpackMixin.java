package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlot;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("reliable_backpacks")
@Mixin(targets = "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$SlotWrapper")
public class CreativeSlotWrapperBackpackMixin {
    @Shadow
    @Final
    Slot target;

    @ModifyReturnValue(method = "isActive", at = @At("RETURN"))
    private boolean spicedcider$hideBackpackSlots(boolean original) {
        return original && !(this.target instanceof WornBackpackSlot);
    }
}
