package com.evandev.spicedcider.compat.toilandtrouble;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronContents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public record WaterCauldronItemHandler(Level level, BlockPos pos) implements IItemHandler {
    private static final CauldronContents WATER = new CauldronContents(Potions.WATER);
    private static final ResourceLocation BREWING_CAULDRON = ResourceLocation.fromNamespaceAndPath("toil_and_trouble", "cauldron");

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
            BlockState water = this.level.getBlockState(this.pos);
            BlockState brewing = BuiltInRegistries.BLOCK.get(BREWING_CAULDRON).defaultBlockState()
                    .setValue(BrewingCauldronBlock.POTION_QUANTITY, water.getValue(LayeredCauldronBlock.LEVEL))
                    .setValue(BrewingCauldronBlock.CONTENTS, BrewingCauldronBlock.ContentsProperty.WATER);
            this.level.setBlockAndUpdate(this.pos, brewing);
            CauldronBlockEntity cauldron = new CauldronBlockEntity(this.pos, brewing, WATER);
            this.level.setBlockEntity(cauldron);
            cauldron.setItem(0, stack.copyWithCount(1));
            cauldron.setChanged();
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
        return !stack.isEmpty() && this.level.getBlockState(this.pos).is(Blocks.WATER_CAULDRON)
                && CauldronItemHandler.startsBrew(this.level, WATER, stack);
    }
}
