package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackContainer;
import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackSlot;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("reliable_backpacks")
@Mixin(InventoryMenu.class)
public abstract class InventoryMenuBackpackMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    @Unique
    @Nullable
    private WornBackpackContainer spicedcider$backpack;

    public InventoryMenuBackpackMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$addBackpackSlots(Inventory playerInventory, boolean active, Player owner, CallbackInfo ci) {
        if ((Object) this.getClass() != InventoryMenu.class) return;
        this.spicedcider$backpack = new WornBackpackContainer(owner);
        for (int i = 0; i < WornBackpackContainer.SIZE; i++) {
            this.addSlot(new WornBackpackSlot(this.spicedcider$backpack, i));
        }
    }

    @WrapMethod(method = "quickMoveStack")
    private ItemStack spicedcider$quickMoveWithBackpack(Player player, int index, Operation<ItemStack> original) {
        if (this.spicedcider$backpack == null || !this.spicedcider$backpack.isOpen()) {
            return original.call(player, index);
        }

        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(copy);
        int first = WornBackpackSlot.FIRST;
        int end = WornBackpackSlot.END;

        if (index == InventoryMenu.RESULT_SLOT) {
            if (!this.moveItemStackTo(stack, InventoryMenu.INV_SLOT_START, InventoryMenu.USE_ROW_SLOT_END, true)
                    && !this.moveItemStackTo(stack, first, end, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, copy);
        } else if (index < InventoryMenu.INV_SLOT_START) {
            if (!this.moveItemStackTo(stack, InventoryMenu.INV_SLOT_START, InventoryMenu.USE_ROW_SLOT_END, false)
                    && !this.moveItemStackTo(stack, first, end, false)) {
                return ItemStack.EMPTY;
            }
        } else if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !this.slots.get(8 - equipmentSlot.getIndex()).hasItem()) {
            int armor = 8 - equipmentSlot.getIndex();
            if (!this.moveItemStackTo(stack, armor, armor + 1, false)) return ItemStack.EMPTY;
        } else if (equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(InventoryMenu.SHIELD_SLOT).hasItem()) {
            if (!this.moveItemStackTo(stack, InventoryMenu.SHIELD_SLOT, InventoryMenu.SHIELD_SLOT + 1, false)) return ItemStack.EMPTY;
        } else if (index < InventoryMenu.INV_SLOT_END) {
            if (!this.moveItemStackTo(stack, first, end, false)
                    && !this.moveItemStackTo(stack, InventoryMenu.USE_ROW_SLOT_START, InventoryMenu.USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < InventoryMenu.USE_ROW_SLOT_END) {
            if (!this.moveItemStackTo(stack, InventoryMenu.INV_SLOT_START, InventoryMenu.INV_SLOT_END, false)
                    && !this.moveItemStackTo(stack, first, end, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index == InventoryMenu.SHIELD_SLOT) {
            if (!this.moveItemStackTo(stack, InventoryMenu.INV_SLOT_START, InventoryMenu.USE_ROW_SLOT_END, false)) return ItemStack.EMPTY;
        } else if (!this.moveItemStackTo(stack, InventoryMenu.INV_SLOT_START, InventoryMenu.USE_ROW_SLOT_END, true)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY, copy);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, stack);
        if (index == InventoryMenu.RESULT_SLOT) {
            player.drop(stack, false);
        }
        return copy;
    }
}
