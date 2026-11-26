package com.evandev.spicedcider.compat.create;

import com.evandev.spicedcider.registry.ModAttachments;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.evandev.spicedcider.content.casting.BasinCastingRecipe;
import com.evandev.spicedcider.content.casting.BasinCastState;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BasinCasting {
    private static final int CHECK_INTERVAL = 5;

    public static void tick(BasinBlockEntity basin) {
        Level level = basin.getLevel();
        if (level == null || level.isClientSide) return;
        BasinCastState state = basin.getData(ModAttachments.BASIN_CAST);
        if (state.isEmpty() && level.getGameTime() % CHECK_INTERVAL != 0) return;
        if (level.getBlockEntity(basin.getBlockPos().above(2)) instanceof BasinOperatingBlockEntity) return;
        IFluidHandler tanks = level.getCapability(Capabilities.FluidHandler.BLOCK, basin.getBlockPos(), null);
        if (tanks == null) return;

        RecipeHolder<BasinCastingRecipe> recipe = state.isEmpty() ? findForTanks(level, tanks) : find(level, state.fluid());
        if (recipe == null) {
            if (!state.isEmpty()) {
                basin.setData(ModAttachments.BASIN_CAST, BasinCastState.EMPTY);
                basin.sendData();
            }
            return;
        }
        int needed = recipe.value().amount();
        FluidStack held = state.fluid();
        if (held.getAmount() < needed) {
            FluidStack want = held.isEmpty() ? firstMatching(tanks, recipe.value()) : held.copyWithAmount(needed - held.getAmount());
            if (want.isEmpty()) return;
            FluidStack drained = tanks.drain(want.copyWithAmount(Math.min(want.getAmount(), needed - held.getAmount())), IFluidHandler.FluidAction.EXECUTE);
            if (drained.isEmpty()) return;
            held = held.isEmpty() ? drained : held.copyWithAmount(held.getAmount() + drained.getAmount());
            basin.setData(ModAttachments.BASIN_CAST, new BasinCastState(held, 0));
            basin.notifyChangeOfContents();
            basin.sendData();
            return;
        }
        int cooling = state.cooling() + 1;
        if (cooling < recipe.value().time()) {
            basin.setData(ModAttachments.BASIN_CAST, new BasinCastState(held, cooling));
            if (cooling == 1) basin.sendData();
            return;
        }
        ItemStack result = recipe.value().result().copy();
        if (!basin.acceptOutputs(List.of(result), List.of(), true)) return;
        basin.acceptOutputs(List.of(result.copy()), List.of(), false);
        basin.setData(ModAttachments.BASIN_CAST, BasinCastState.EMPTY);
        basin.notifyChangeOfContents();
        basin.sendData();
        level.playSound(null, basin.getBlockPos(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 1.2F);
    }

    public static boolean isCastResult(Level level, ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (RecipeHolder<BasinCastingRecipe> holder : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.BASIN_CASTING_TYPE.get())) {
            if (ItemStack.isSameItem(holder.value().result(), stack)) return true;
        }
        return false;
    }

    @Nullable
    private static RecipeHolder<BasinCastingRecipe> findForTanks(Level level, IFluidHandler tanks) {
        for (int i = 0; i < tanks.getTanks(); i++) {
            FluidStack fluid = tanks.getFluidInTank(i);
            if (fluid.isEmpty()) continue;
            RecipeHolder<BasinCastingRecipe> recipe = find(level, fluid);
            if (recipe != null) return recipe;
        }
        return null;
    }

    @Nullable
    public static RecipeHolder<BasinCastingRecipe> find(Level level, FluidStack fluid) {
        for (RecipeHolder<BasinCastingRecipe> holder : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.BASIN_CASTING_TYPE.get())) {
            if (holder.value().accepts(fluid)) return holder;
        }
        return null;
    }

    private static FluidStack firstMatching(IFluidHandler tanks, BasinCastingRecipe recipe) {
        for (int i = 0; i < tanks.getTanks(); i++) {
            FluidStack fluid = tanks.getFluidInTank(i);
            if (!fluid.isEmpty() && recipe.accepts(fluid)) return fluid.copy();
        }
        return FluidStack.EMPTY;
    }
}
