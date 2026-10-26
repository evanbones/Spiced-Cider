package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlot;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import de.keksuccino.fancymenu.customization.element.elements.button.vanillawidget.VanillaWidgetElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("fancymenu")
@Mixin(value = VanillaWidgetElement.class, remap = false)
public class FancyMenuVanillaWidgetBackpackMixin {
    @ModifyExpressionValue(
            method = "updateWidgetPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lde/keksuccino/fancymenu/customization/element/elements/button/vanillawidget/VanillaWidgetElement;getAbsoluteY()I"
            )
    )
    private int spicedcider$followBackpackScreen(int y) {
        if (Minecraft.getInstance().screen instanceof InventoryScreen screen && WornBackpackSlot.isOpen(screen.getMenu())) {
            return y - WornBackpackSlot.SCREEN_SHIFT;
        }
        return y;
    }
}
