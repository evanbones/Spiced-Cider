package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackContainer;
import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlots;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("reliable_backpacks")
@Mixin(CraftingMenu.class)
public abstract class CraftingMenuBackpackMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> {

    public CraftingMenuBackpackMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    private void spicedcider$addBackpackSlots(int containerId, Inventory playerInventory, ContainerLevelAccess access, CallbackInfo ci) {
        if (this.getType() != MenuType.CRAFTING || !WornBackpackContainer.enabled()) return;
        WornBackpackSlots.addTo(playerInventory.player, this::addSlot);
    }

    @WrapMethod(method = "quickMoveStack")
    private ItemStack spicedcider$quickMoveFromBackpack(Player player, int index, Operation<ItemStack> original) {
        if (WornBackpackSlots.isBackpackSlot(this, index)) {
            return WornBackpackSlots.moveFromBackpack(this, player, index, this::moveItemStackTo);
        }
        return original.call(player, index);
    }
}
