package com.evandev.spicedcider.content.casting;

import com.evandev.spicedcider.registry.ModBlockEntities;
import com.evandev.spicedcider.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrucibleBlockEntity extends BlockEntity {
    public static final int INPUTS = 4;
    public static final int SLOTS = 4;
    public static final int CAPACITY = 1000;

    private final ItemStackHandler items = new ItemStackHandler(SLOTS) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return level == null || Casting.isMeltable(level, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            changed();
        }
    };
    private FluidStack fluid = FluidStack.EMPTY;
    private int meltProgress;
    private int meltTotal;
    private FluidStack meltFluid = FluidStack.EMPTY;
    private int meltFinal;
    private float shown;
    private float shownO;
    private boolean shownReady;
    private CastingHeat heat = CastingHeat.NONE;

    private final IItemHandler automation = new IItemHandler() {
        @Override
        public int getSlots() {
            return SLOTS;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return items.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return items.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return items.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return items.isItemValid(slot, stack);
        }
    };

    private final IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return fluid;
        }

        @Override
        public int getTankCapacity(int tank) {
            return CAPACITY;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return level != null && Casting.castingRecipeExists(level, stack);
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            if (resource.isEmpty() || !isFluidValid(0, resource)) {
                return 0;
            }
            if (!fluid.isEmpty() && !FluidStack.isSameFluidSameComponents(fluid, resource)) {
                return 0;
            }
            int accepted = Math.min(resource.getAmount(), CAPACITY - fluid.getAmount());
            if (accepted > 0 && action.execute()) {
                if (fluid.isEmpty()) {
                    fluid = resource.copyWithAmount(accepted);
                } else {
                    fluid.grow(accepted);
                }
                changed();
            }
            return Math.max(accepted, 0);
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid)) {
                return FluidStack.EMPTY;
            }
            return drain(resource.getAmount(), action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            int amount = Math.min(maxDrain, fluid.getAmount());
            if (amount <= 0) {
                return FluidStack.EMPTY;
            }
            FluidStack drained = fluid.copyWithAmount(amount);
            if (action.execute()) {
                fluid.shrink(amount);
                if (fluid.isEmpty()) {
                    fluid = FluidStack.EMPTY;
                }
                changed();
            }
            return drained;
        }
    };

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUCIBLE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        crucible.heat = CastingHeat.below(level, pos);
        crucible.tickMelting(level, pos);
    }

    private void tickMelting(Level level, BlockPos pos) {
        Match match = this.heat == CastingHeat.NONE || !hasInputs() ? null : find(level);
        if (match == null) {
            boolean wasMelting = this.meltTotal > 0;
            this.meltProgress = 0;
            this.meltTotal = 0;
            this.meltFluid = FluidStack.EMPTY;
            if (wasMelting) {
                changed();
            }
            return;
        }
        FluidStack result = match.recipe().value().result();
        int last = match.drained() > 0 ? 0 : this.fluid.getAmount();
        boolean started = this.meltTotal != match.recipe().value().time() || this.meltFinal != last + result.getAmount() || !FluidStack.isSameFluidSameComponents(this.meltFluid, result);
        this.meltTotal = match.recipe().value().time();
        this.meltFluid = result.copy();
        this.meltFinal = last + result.getAmount();
        this.meltProgress++;
        if (started) {
            changed();
        }
        if (level instanceof ServerLevel server && this.meltProgress % 20 == 0) {
            server.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.85, pos.getZ() + 0.5, 2, 0.08, 0.02, 0.08, 0.0);
        }
        if (this.meltProgress < this.meltTotal) {
            return;
        }
        this.meltProgress = 0;
        for (int slot : match.slots()) {
            if (slot >= 0) {
                this.items.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
        if (match.drained() > 0) {
            this.fluid = FluidStack.EMPTY;
        }
        this.meltTotal = 0;
        this.meltFluid = FluidStack.EMPTY;
        if (this.fluid.isEmpty()) {
            this.fluid = result.copy();
        } else {
            this.fluid.grow(result.getAmount());
        }
        level.playSound(null, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.6F, 0.9F + level.random.nextFloat() * 0.2F);
        changed();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity crucible) {
        if (crucible.meltTotal > 0 && crucible.meltProgress < crucible.meltTotal) {
            crucible.meltProgress++;
        }
        float target = crucible.fluid.getAmount();
        if (crucible.meltTotal > 0) {
            target += (crucible.meltFinal - target) * crucible.meltFraction();
        }
        if (!crucible.shownReady) {
            crucible.shownReady = true;
            crucible.shown = target;
        }
        crucible.shownO = crucible.shown;
        float step = Math.max(2.0F, Math.abs(target - crucible.shown) * 0.2F);
        crucible.shown = crucible.shown < target ? Math.min(target, crucible.shown + step) : Math.max(target, crucible.shown - step);
    }

    public float meltFraction() {
        return this.meltTotal <= 0 ? 0.0F : Math.min(1.0F, (float) this.meltProgress / this.meltTotal);
    }

    public float displayAmount(float partialTick) {
        return this.shownO + (this.shown - this.shownO) * partialTick;
    }

    public FluidStack displayFluid() {
        if (this.fluid.isEmpty() || this.meltTotal > 0 && !this.meltFluid.isEmpty() && meltFraction() > 0.5F) {
            return this.meltFluid.isEmpty() ? this.fluid : this.meltFluid;
        }
        return this.fluid;
    }

    private record Match(RecipeHolder<CrucibleMeltingRecipe> recipe, int[] slots, int drained) {
    }

    @Nullable
    private Match find(Level level) {
        List<ItemStack> stacks = inputs();
        for (RecipeHolder<CrucibleMeltingRecipe> holder : Casting.meltingRecipes(level)) {
            CrucibleMeltingRecipe recipe = holder.value();
            if (!this.heat.reaches(recipe.heat())) {
                continue;
            }
            if (!this.fluid.isEmpty() && !FluidStack.isSameFluidSameComponents(this.fluid, recipe.result())) {
                Match alloy = alloyWithTank(level, holder, stacks);
                if (alloy != null) {
                    return alloy;
                }
                continue;
            }
            if (this.fluid.getAmount() + recipe.result().getAmount() > CAPACITY) {
                continue;
            }
            int[] slots = recipe.match(stacks);
            if (slots != null) {
                return new Match(holder, slots, 0);
            }
        }
        return null;
    }

    @Nullable
    private Match alloyWithTank(Level level, RecipeHolder<CrucibleMeltingRecipe> holder, List<ItemStack> stacks) {
        CrucibleMeltingRecipe recipe = holder.value();
        int count = recipe.ingredients().size();
        if (count < 2 || recipe.result().getAmount() > CAPACITY) {
            return null;
        }
        int[] values = new int[count];
        boolean any = false;
        for (int i = 0; i < count; i++) {
            values[i] = Casting.fluidValue(level, recipe.ingredients().get(i), this.fluid);
            any |= values[i] > 0;
        }
        if (!any) {
            return null;
        }
        int all = (1 << count) - 1;
        for (int skipped = 1; skipped < all; skipped++) {
            int total = 0;
            boolean valid = true;
            for (int i = 0; i < count && valid; i++) {
                if ((skipped & (1 << i)) != 0) {
                    valid = values[i] > 0;
                    total += values[i];
                }
            }
            if (!valid || total != this.fluid.getAmount()) {
                continue;
            }
            int[] slots = recipe.match(stacks, skipped);
            if (slots != null) {
                return new Match(holder, slots, total);
            }
        }
        return null;
    }

    private List<ItemStack> inputs() {
        NonNullList<ItemStack> list = NonNullList.withSize(INPUTS, ItemStack.EMPTY);
        for (int i = 0; i < INPUTS; i++) {
            list.set(i, this.items.getStackInSlot(i));
        }
        return list;
    }

    public boolean hasInputs() {
        for (int i = 0; i < INPUTS; i++) {
            if (!this.items.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<ItemStack> getInputs() {
        return inputs();
    }

    public FluidStack getFluid() {
        return this.fluid;
    }

    public void setFluid(FluidStack fluid) {
        this.fluid = fluid.isEmpty() ? FluidStack.EMPTY : fluid;
        changed();
    }

    public ItemStackHandler getItems() {
        return this.items;
    }

    public IItemHandler getItemHandler() {
        return this.automation;
    }

    public IFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }

    public NonNullList<ItemStack> drops() {
        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < SLOTS; i++) {
            list.add(this.items.getStackInSlot(i));
        }
        return list;
    }

    private void changed() {
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            this.level.updateNeighbourForOutputSignal(this.worldPosition, state.getBlock());
        }
    }

    public boolean insert(ItemStack stack) {
        for (int i = 0; i < INPUTS; i++) {
            if (this.items.getStackInSlot(i).isEmpty() && this.items.isItemValid(i, stack)) {
                this.items.setStackInSlot(i, stack.copyWithCount(1));
                return true;
            }
        }
        return false;
    }

    public ItemStack takeLast() {
        for (int i = INPUTS - 1; i >= 0; i--) {
            ItemStack stack = this.items.getStackInSlot(i);
            if (!stack.isEmpty()) {
                this.items.setStackInSlot(i, ItemStack.EMPTY);
                this.meltProgress = 0;
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            ItemStackHandler saved = new ItemStackHandler(SLOTS);
            saved.deserializeNBT(registries, tag.getCompound("Inventory"));
            for (int i = 0; i < SLOTS; i++) {
                this.items.setStackInSlot(i, i < saved.getSlots() ? saved.getStackInSlot(i) : ItemStack.EMPTY);
            }
        }
        this.fluid = tag.contains("Fluid") ? FluidStack.parseOptional(registries, tag.getCompound("Fluid")) : FluidStack.EMPTY;
        this.meltProgress = tag.getInt("MeltProgress");
        this.meltTotal = tag.getInt("MeltTotal");
        this.meltFinal = tag.getInt("MeltFinal");
        this.meltFluid = tag.contains("MeltFluid") ? FluidStack.parseOptional(registries, tag.getCompound("MeltFluid")) : FluidStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", this.items.serializeNBT(registries));
        if (!this.fluid.isEmpty()) {
            tag.put("Fluid", this.fluid.save(registries));
        }
        tag.putInt("MeltProgress", this.meltProgress);
        tag.putInt("MeltTotal", this.meltTotal);
        tag.putInt("MeltFinal", this.meltFinal);
        if (!this.meltFluid.isEmpty()) {
            tag.put("MeltFluid", this.meltFluid.save(registries));
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        NonNullList<ItemStack> list = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(list);
        for (int i = 0; i < SLOTS; i++) {
            this.items.setStackInSlot(i, list.get(i));
        }
        this.fluid = input.getOrDefault(ModDataComponents.FLUID_CONTENTS.get(), SimpleFluidContent.EMPTY).copy();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        NonNullList<ItemStack> list = drops();
        if (list.stream().anyMatch(stack -> !stack.isEmpty())) {
            builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
        }
        if (!this.fluid.isEmpty()) {
            builder.set(ModDataComponents.FLUID_CONTENTS.get(), SimpleFluidContent.copyOf(this.fluid));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("Inventory");
        tag.remove("Fluid");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
