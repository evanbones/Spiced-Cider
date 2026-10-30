package com.evandev.spicedcider.mixin.vanity.client;

import com.evandev.spicedcider.interfaces.ITrimStylingMenu;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.vanity.client.components.list.StylesListWidget;
import tech.thatgravyboat.vanity.client.screen.StylingScreen;
import tech.thatgravyboat.vanity.common.menu.StylingMenu;

@IfModLoaded("vanity")
@Mixin(value = StylingScreen.class, remap = false)
public abstract class StylingScreenTrimMixin extends AbstractContainerScreen<StylingMenu> {

    @Shadow
    private StylesListWidget list;

    private StylingScreenTrimMixin(StylingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(method = "slotChanged", at = @At("TAIL"))
    private void spicedcider$refreshPreviews(AbstractContainerMenu changed, int slot, ItemStack stack, CallbackInfo ci) {
        if (slot != ITrimStylingMenu.ADDITION_SLOT || this.list == null) return;
        ((StylesListWidgetAccessor) this.list).spicedcider$setOldHash(-1);
        this.list.addAll(this.menu.styles(), this.menu.getInput());
        this.list.select(this.menu.getResult());
    }
}
