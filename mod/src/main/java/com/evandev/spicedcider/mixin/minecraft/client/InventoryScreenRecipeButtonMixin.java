package com.evandev.spicedcider.mixin.minecraft.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenRecipeButtonMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
    @Unique
    private static final int SPICEDCIDER$RECIPE_BUTTON_X = 125;

    @Unique
    @Nullable
    private ImageButton spicedcider$bookButton;

    public InventoryScreenRecipeButtonMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @ModifyExpressionValue(method = "init", at = @At(value = "NEW", target = "net/minecraft/client/gui/components/ImageButton"))
    private ImageButton spicedcider$captureBookButton(ImageButton button) {
        this.spicedcider$bookButton = button;
        return button;
    }

    @Inject(method = "renderBg", at = @At("HEAD"))
    private void spicedcider$placeBookButton(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.spicedcider$bookButton != null) {
            this.spicedcider$bookButton.setX(this.leftPos + SPICEDCIDER$RECIPE_BUTTON_X);
        }
    }
}
