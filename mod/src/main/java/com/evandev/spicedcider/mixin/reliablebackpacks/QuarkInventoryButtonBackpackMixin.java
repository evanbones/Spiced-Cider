package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlot;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.base.client.handler.InventoryButtonHandler;

@IfModLoaded("quark")
@Mixin(value = InventoryButtonHandler.class, remap = false)
public class QuarkInventoryButtonBackpackMixin {
    @ModifyExpressionValue(
            method = "applyProviders",
            at = @At(value = "CONSTANT", args = "classValue=org/violetmoon/quark/addons/oddities/client/screen/BackpackInventoryScreen")
    )
    private static boolean spicedcider$liftAboveBackpackRows(boolean original, @Local(argsOnly = true) AbstractContainerScreen<?> screen) {
        return original || screen instanceof InventoryScreen inventory && WornBackpackSlot.isOpen(inventory.getMenu());
    }
}
