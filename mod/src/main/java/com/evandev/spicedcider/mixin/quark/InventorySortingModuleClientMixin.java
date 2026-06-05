package com.evandev.spicedcider.mixin.quark;

import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.base.client.handler.InventoryButtonHandler;
import org.violetmoon.quark.base.network.message.SortInventoryMessage;
import org.violetmoon.quark.content.management.client.screen.widgets.MiniInventoryButton;
import org.violetmoon.quark.content.management.module.InventorySortingModule;

import java.util.function.BooleanSupplier;

@Mixin(InventorySortingModule.Client.class)
public class InventorySortingModuleClientMixin {

    @Inject(method = "provider", at = @At("HEAD"), cancellable = true, remap = false)
    private void hidePlayerInventorySortButton(String tooltip, boolean forcePlayer, BooleanSupplier condition, CallbackInfoReturnable<InventoryButtonHandler.ButtonProvider> cir) {
        if ("sort".equals(tooltip)) {
            cir.setReturnValue((parent, x, y) -> {
                if (!condition.getAsBoolean()) {
                    return null;
                }

                MiniInventoryButton hiddenButton = new MiniInventoryButton(
                        parent,
                        0,
                        x,
                        y,
                        "quark.gui.button.sort",
                        (b) -> PacketDistributor.sendToServer(new SortInventoryMessage(forcePlayer))
                );

                hiddenButton.visible = false;
                hiddenButton.active = false;

                return hiddenButton;
            });
        }
    }
}