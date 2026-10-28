package com.evandev.spicedcider.compat.toilandtrouble;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronContents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToilAndTroubleCauldrons {

    public static boolean is(BlockEntity blockEntity) {
        return blockEntity instanceof CauldronBlockEntity;
    }

    public static boolean isEmpty(BlockEntity blockEntity, BlockState state) {
        return blockEntity instanceof CauldronBlockEntity && state.getOptionalValue(BrewingCauldronBlock.POTION_QUANTITY).orElse(0) <= 0;
    }

    @Nullable
    public static Fluid fullFluid(BlockEntity blockEntity, BlockState state) {
        if (!(blockEntity instanceof CauldronBlockEntity cauldron)) {
            return null;
        }
        int quantity = state.getOptionalValue(BrewingCauldronBlock.POTION_QUANTITY).orElse(0);
        if (quantity <= 0) {
            return Fluids.EMPTY;
        }
        if (quantity < 3) {
            return null;
        }
        CauldronContents contents = cauldron.getContents();
        if (contents.potion().isPresent() && contents.potion().get().is(Potions.WATER) && contents.customEffects().isEmpty()) {
            return Fluids.WATER;
        }
        if (contents.is("milk")) {
            return NeoForgeMod.MILK.get();
        }
        if (contents.is("lava")) {
            return Fluids.LAVA;
        }
        if (contents.is("honey")) {
            return BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("create", "honey"));
        }
        return null;
    }

    public static List<ItemStack> items(BlockEntity blockEntity) {
        return blockEntity instanceof CauldronBlockEntity cauldron ? cauldron.getItems() : List.of();
    }

    public static void changed(BlockEntity blockEntity) {
        if (blockEntity instanceof CauldronBlockEntity cauldron) {
            cauldron.getItems().removeIf(ItemStack::isEmpty);
            cauldron.setChanged();
        }
    }
}
