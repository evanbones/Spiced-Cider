package com.evandev.spicedcider.compat.toilandtrouble;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.recipe.BrewingRecipeInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public record CauldronItemHandler(CauldronBlockEntity cauldron) implements IItemHandler {

    public static boolean startsBrew(Level level, CauldronContents contents, ItemStack stack) {
        for (boolean heated : new boolean[]{true, false}) {
            BrewingRecipeInput input = new BrewingRecipeInput(List.of(stack.copyWithCount(1)), contents, heated);
            if (level.getRecipeManager().getRecipeFor(CauldronModRecipes.BREWING, input, level).isPresent()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!this.isItemValid(slot, stack)) {
            return stack;
        }
        if (!simulate) {
            this.cauldron.setItem(0, stack.copyWithCount(1));
            this.cauldron.setChanged();
        }
        return stack.copyWithCount(stack.getCount() - 1);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return ((AutomatedCauldron) this.cauldron).spicedcider$accepts(stack);
    }
}
