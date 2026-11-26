package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.BackpackScreenBackground;
import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlot;
import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlots;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("reliable_backpacks")
@Mixin(HorseInventoryScreen.class)
public abstract class HorseInventoryScreenBackpackMixin extends AbstractContainerScreen<HorseInventoryMenu> {
    @Unique
    private boolean spicedcider$expanded;

    public HorseInventoryScreenBackpackMixin(HorseInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        if (WornBackpackSlots.first(this.menu) >= 0) {
            this.spicedcider$expanded = WornBackpackSlots.isOpen(this.menu);
            this.imageHeight = this.spicedcider$expanded ? WornBackpackSlot.EXPANDED_HEIGHT : 166;
            this.inventoryLabelY = 72;
            WornBackpackSlots.layout(this.menu, this.spicedcider$expanded);
        }
        super.init();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (WornBackpackSlots.first(this.menu) >= 0 && WornBackpackSlots.isOpen(this.menu) != this.spicedcider$expanded) {
            this.rebuildWidgets();
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
