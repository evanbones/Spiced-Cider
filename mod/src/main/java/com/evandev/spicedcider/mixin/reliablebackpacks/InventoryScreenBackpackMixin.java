package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlot;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("reliable_backpacks")
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenBackpackMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
    @Unique
    private boolean spicedcider$expanded;

    @Unique
    @Nullable
    private ImageButton spicedcider$recipeButton;

    public InventoryScreenBackpackMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void spicedcider$sizeForBackpack(CallbackInfo ci) {
        this.spicedcider$expanded = WornBackpackSlot.isOpen(this.menu);
        this.imageHeight = this.spicedcider$expanded ? WornBackpackSlot.EXPANDED_HEIGHT : 166;
        WornBackpackSlot.layout(this.menu, this.spicedcider$expanded);
    }

    @Inject(method = "containerTick", at = @At("HEAD"))
    private void spicedcider$resizeOnBackpackChange(CallbackInfo ci) {
        if (WornBackpackSlot.isOpen(this.menu) != this.spicedcider$expanded) {
            this.rebuildWidgets();
        }
    }

    @ModifyExpressionValue(method = "init", at = @At(value = "NEW", target = "net/minecraft/client/gui/components/ImageButton"))
    private ImageButton spicedcider$captureRecipeButton(ImageButton button) {
        this.spicedcider$recipeButton = button;
        return button;
    }

    @Inject(method = "renderBg", at = @At("HEAD"))
    private void spicedcider$keepRecipeButtonInPlace(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        ImageButton button = this.spicedcider$recipeButton;
        if (button == null) return;
        int anchor = this.height / 2 - 22;
        if (button.getY() == anchor || button.getY() == anchor - WornBackpackSlot.SCREEN_SHIFT) {
            button.setY(this.spicedcider$expanded ? anchor - WornBackpackSlot.SCREEN_SHIFT : anchor);
        }
    }

    @WrapOperation(
            method = "renderBg",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
            )
    )
    private void spicedcider$drawBackpackRows(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
        if (!this.spicedcider$expanded) {
            original.call(guiGraphics, texture, x, y, u, v, width, height);
            return;
        }
        original.call(guiGraphics, texture, x, y, u, v, width, 83);
        original.call(guiGraphics, texture, x, y + 83, u, v + 83, width, 54);
        original.call(guiGraphics, texture, x, y + 137, u, v + 137, width, 4);
        original.call(guiGraphics, texture, x, y + 141, u, v + 83, width, 83);
    }
}
