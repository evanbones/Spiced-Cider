package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.BackpackScreenBackground;
import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlot;
import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlots;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("reliable_backpacks")
@Mixin(CraftingScreen.class)
public abstract class CraftingScreenBackpackMixin extends AbstractContainerScreen<CraftingMenu> {
    @Unique
    private boolean spicedcider$expanded;

    @Unique
    @Nullable
    private ImageButton spicedcider$recipeButton;

    public CraftingScreenBackpackMixin(CraftingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void spicedcider$sizeForBackpack(CallbackInfo ci) {
        if (WornBackpackSlots.first(this.menu) < 0) return;
        this.spicedcider$expanded = WornBackpackSlots.isOpen(this.menu);
        this.imageHeight = this.spicedcider$expanded ? WornBackpackSlot.EXPANDED_HEIGHT : 166;
        this.inventoryLabelY = 72;
        WornBackpackSlots.layout(this.menu, this.spicedcider$expanded);
    }

    @Inject(method = "containerTick", at = @At("HEAD"))
    private void spicedcider$resizeOnBackpackChange(CallbackInfo ci) {
        if (WornBackpackSlots.first(this.menu) >= 0 && WornBackpackSlots.isOpen(this.menu) != this.spicedcider$expanded) {
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
        int anchor = this.height / 2 - 49;
        if (button.getY() == anchor || button.getY() == anchor - WornBackpackSlot.SCREEN_SHIFT) {
            button.setY(this.spicedcider$expanded ? anchor - WornBackpackSlot.SCREEN_SHIFT : anchor);
        }
    }

    @WrapOperation(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    private void spicedcider$drawBackpackRows(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
        if (!this.spicedcider$expanded) {
            original.call(guiGraphics, texture, x, y, u, v, width, height);
            return;
        }
        BackpackScreenBackground.draw(guiGraphics, texture, x, y, u, v, width, original);
    }
}
