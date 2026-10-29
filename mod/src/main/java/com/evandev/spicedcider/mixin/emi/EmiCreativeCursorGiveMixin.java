package com.evandev.spicedcider.mixin.emi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;

@IfModLoaded("emi")
@IfModLoaded("modulation")
@Mixin(value = EmiScreenManager.class, remap = false)
public class EmiCreativeCursorGiveMixin {

    @WrapOperation(method = "give", constant = @Constant(classValue = CreativeModeInventoryScreen.class))
    private static boolean spicedcider$creativeInventoryScreen(Object screen, Operation<Boolean> original) {
        return original.call(screen) || screen instanceof InventoryScreen;
    }
}
