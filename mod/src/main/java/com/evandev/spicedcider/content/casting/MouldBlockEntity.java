package com.evandev.spicedcider.content.casting;

import com.evandev.spicedcider.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MouldBlockEntity extends BlockEntity {
    private FluidStack fluid = FluidStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;
    private int cooling;
    @Nullable
    private RecipeHolder<MouldCastingRecipe> recipe;

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
            RecipeHolder<MouldCastingRecipe> holder = recipe();
            return holder == null ? 1000 : holder.value().amount();
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return result.isEmpty() && level != null && Casting.castingRecipe(level, shape(), stack) != null;
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return MouldBlockEntity.this.fill(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            return FluidStack.EMPTY;
        }
    };

    private final IItemHandler itemHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return result;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (result.isEmpty() || amount <= 0) {
                return ItemStack.EMPTY;
            }
            ItemStack taken = result.copyWithCount(Math.min(amount, result.getCount()));
            if (!simulate) {
                result.shrink(taken.getCount());
                if (result.isEmpty()) {
                    result = ItemStack.EMPTY;
                }
                changed();
            }
            return taken;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
    };

    public MouldBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOULD.get(), pos, state);
    }

    public MouldShape shape() {
        return getBlockState().getBlock() instanceof MouldBlock mould ? mould.shape() : MouldShape.INGOT;
    }

    @Nullable
    private RecipeHolder<MouldCastingRecipe> recipe() {
        if (this.fluid.isEmpty() || this.level == null) {
            this.recipe = null;
            return null;
        }
        if (this.recipe == null || !this.recipe.value().accepts(shape(), this.fluid)) {
            this.recipe = Casting.castingRecipe(this.level, shape(), this.fluid);
        }
        return this.recipe;
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !this.result.isEmpty() || this.level == null) {
            return 0;
        }
        if (!this.fluid.isEmpty() && !FluidStack.isSameFluidSameComponents(this.fluid, resource)) {
            return 0;
        }
        RecipeHolder<MouldCastingRecipe> holder = this.fluid.isEmpty() ? Casting.castingRecipe(this.level, shape(), resource) : recipe();
        if (holder == null) {
            return 0;
        }
        int accepted = Math.min(resource.getAmount(), holder.value().amount() - this.fluid.getAmount());
        if (accepted <= 0) {
            return 0;
        }
        if (action.execute()) {
            if (this.fluid.isEmpty()) {
                this.fluid = resource.copyWithAmount(accepted);
                this.recipe = holder;
            } else {
                this.fluid.grow(accepted);
            }
            this.cooling = 0;
            changed();
        }
        return accepted;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MouldBlockEntity mould) {
        if (mould.fluid.isEmpty()) {
            mould.cooling = 0;
            return;
        }
        RecipeHolder<MouldCastingRecipe> holder = mould.recipe();
        if (holder == null) {
            if (!level.isClientSide()) {
                mould.fluid = FluidStack.EMPTY;
                mould.changed();
            }
            return;
        }
        if (mould.fluid.getAmount() < holder.value().amount() || !mould.result.isEmpty()) {
            mould.cooling = 0;
            return;
        }
        mould.cooling++;
        if (level.isClientSide()) {
            return;
        }
        if (mould.cooling % 20 == 1 && level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 1, 0.15, 0.02, 0.15, 0.0);
        }
        if (mould.cooling >= holder.value().time()) {
            mould.result = holder.value().result().copy();
            mould.fluid = FluidStack.EMPTY;
            mould.cooling = 0;
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.35F, 1.5F + level.random.nextFloat() * 0.2F);
            mould.changed();
        }
    }

    public float coolingFraction(float partialTick) {
        RecipeHolder<MouldCastingRecipe> holder = recipe();
        if (holder == null || this.fluid.getAmount() < holder.value().amount()) {
            return 0.0F;
        }
        return Math.min(1.0F, (this.cooling + partialTick) / holder.value().time());
    }

    public float fillFraction() {
        RecipeHolder<MouldCastingRecipe> holder = recipe();
        if (holder == null) {
            return 0.0F;
        }
        return Math.min(1.0F, (float) this.fluid.getAmount() / holder.value().amount());
    }

    public FluidStack getFluid() {
        return this.fluid;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public ItemStack takeResult() {
        ItemStack taken = this.result;
        this.result = ItemStack.EMPTY;
        changed();
        return taken;
    }

    public void setContents(FluidStack fluid, ItemStack result) {
        this.fluid = fluid.copy();
        this.result = result.copy();
        this.cooling = 0;
        this.recipe = null;
        changed();
    }

    public void clearFluid() {
        this.fluid = FluidStack.EMPTY;
        this.cooling = 0;
        changed();
    }

    public IFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }

    private void changed() {
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            this.level.updateNeighbourForOutputSignal(this.worldPosition, state.getBlock());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.fluid = tag.contains("Fluid") ? FluidStack.parseOptional(registries, tag.getCompound("Fluid")) : FluidStack.EMPTY;
        this.result = tag.contains("Result") ? ItemStack.parseOptional(registries, tag.getCompound("Result")) : ItemStack.EMPTY;
        this.cooling = tag.getInt("Cooling");
        this.recipe = null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.fluid.isEmpty()) {
            tag.put("Fluid", this.fluid.save(registries));
        }
        if (!this.result.isEmpty()) {
            tag.put("Result", this.result.save(registries));
        }
        tag.putInt("Cooling", this.cooling);
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
