package com.evandev.spicedcider.mixin.easyanvils;

import com.evandev.spicedcider.mixin.minecraft.client.AnvilScreenAccessor;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("easyanvils")
@Mixin(targets = "fuzs.easyanvils.client.gui.screens.inventory.ModAnvilScreen", remap = false)
public abstract class ModAnvilScreenMixin extends AnvilScreen {

    private ModAnvilScreenMixin(AnvilMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(method = "subInit()V", at = @At("TAIL"))
    private void spicedcider$dropRenaming(CallbackInfo ci) {
        for (GuiEventListener child : List.copyOf(this.children())) {
            if (child.getClass().getSimpleName().equals("FormattingGuideWidget")) {
                this.removeWidget(child);
            }
        }
        this.titleLabelY = 24;
        this.spicedcider$hideName();
    }

    @Inject(method = "slotChanged(Lnet/minecraft/world/inventory/AbstractContainerMenu;ILnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private void spicedcider$keepNameHidden(AbstractContainerMenu menu, int slot, ItemStack stack, CallbackInfo ci) {
        this.spicedcider$hideName();
    }

    private void spicedcider$hideName() {
        EditBox name = ((AnvilScreenAccessor) this).spicedcider$getName();
        name.setEditable(false);
        name.setVisible(false);
        name.setFocused(false);
    }
}
