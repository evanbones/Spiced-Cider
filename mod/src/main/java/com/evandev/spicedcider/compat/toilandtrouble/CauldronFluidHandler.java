package com.evandev.spicedcider.compat.toilandtrouble;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronContents;
import com.evandev.spicedcider.content.brewing.CauldronBrewing;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public record CauldronFluidHandler(CauldronBlockEntity cauldron) implements IFluidHandler {
    private static final int BOTTLE = 250;

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.current();
    }

    @Override
    public int getTankCapacity(int tank) {
        return BOTTLE * 3;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack current = this.current();
        if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, current)) {
            return FluidStack.EMPTY;
        }
        return this.drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack current = this.current();
        if (current.isEmpty() || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        int amount = Math.min(maxDrain, current.getAmount());
        if (action.execute()) {
            this.take(amount);
        }
        return current.copyWithAmount(amount);
    }

    private FluidStack current() {
        CauldronContents contents = this.cauldron.getContents();
        boolean brewing = this.cauldron.getBlockState().getOptionalValue(BrewingCauldronBlock.BREWING).orElse(false);
        if (!contents.isPotion() || CauldronBrewing.isWater(contents.potion()) || CauldronBrewing.isClear(contents.potion())
                || brewing || !this.cauldron.getItems().isEmpty()) {
            return FluidStack.EMPTY;
        }
        int amount = this.cauldron.getFillLevel() * BOTTLE - ((AutomatedCauldron) this.cauldron).spicedcider$drained();
        if (amount <= 0) {
            return FluidStack.EMPTY;
        }
        BottleType type = this.cauldron.isPotionLingering() ? BottleType.LINGERING
                : this.cauldron.isPotionSplash() ? BottleType.SPLASH : BottleType.REGULAR;
        return PotionFluidHandler.getFluidFromPotion(contents.toPotionContents(), type, amount);
    }

    private void take(int amount) {
        AutomatedCauldron automated = (AutomatedCauldron) this.cauldron;
        int drained = automated.spicedcider$drained() + amount;
        int level = this.cauldron.getFillLevel();
        int start = level;
        while (drained >= BOTTLE && level > 0) {
            drained -= BOTTLE;
            level--;
        }
        automated.spicedcider$setDrained(level == 0 ? 0 : drained);
        if (level != start) {
            this.cauldron.setFillLevelAndUpdate(level);
        } else {
            this.cauldron.setChanged();
        }
    }
}
