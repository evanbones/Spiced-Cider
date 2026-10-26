package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.modulation.api.tweaks.IntTweak;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlot;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("modulation")
@Mixin(value = IntTweak.class, remap = false)
public class ModulationClearButtonBackpackMixin {
    @ModifyReturnValue(method = "get()Ljava/lang/Integer;", at = @At("RETURN"))
    private Integer spicedcider$followBackpackRows(Integer value) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && minecraft.screen instanceof InventoryScreen screen
                && (Object) this == VanillaGuiModule.CLEAR_BUTTON_Y && WornBackpackSlot.isOpen(screen.getMenu())) {
            return value + WornBackpackSlot.ROWS_OFFSET;
        }
        return value;
    }
}
