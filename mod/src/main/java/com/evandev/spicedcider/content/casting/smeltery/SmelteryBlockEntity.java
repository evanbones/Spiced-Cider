package com.evandev.spicedcider.content.casting.smeltery;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.casting.Casting;
import com.evandev.spicedcider.content.casting.CrucibleMeltingRecipe;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SmelteryBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUTS = 8;
    public static final int FUEL_CAPACITY = 4000;
    public static final int METAL_CAPACITY = 9000;
    public static final int DATA_COUNT = INPUTS * 2;
    public static final TagKey<Fluid> FUELS = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "smeltery_fuels"));
    private static final int ALLOY_INTERVAL = 5;
    public static final int POUR_UNIT = 250;

    private final ItemStackHandler items = new ItemStackHandler(INPUTS) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return level == null || meltingRecipe(level, stack) != null || isAdditive(level, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            progress[slot] = 0;
            changed();
        }
    };
    private final int[] progress = new int[INPUTS];
    private final int[] total = new int[INPUTS];
    private FluidStack fuel = FluidStack.EMPTY;
    private final List<FluidStack> metals = new ArrayList<>();
    private boolean active;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index < INPUTS ? progress[index] : total[index - INPUTS];
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    private final IItemHandler automation = new IItemHandler() {
        @Override
        public int getSlots() {
            return INPUTS;
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
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return items.isItemValid(slot, stack);
        }
    };

    private final IFluidHandler fluidHandler = new IFluidHandler() {
        @Override
        public int getTanks() {
            return 1 + Math.max(1, metals.size());
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            if (tank == 0) {
                return fuel;
            }
            return tank - 1 < metals.size() ? metals.get(tank - 1) : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? FUEL_CAPACITY : METAL_CAPACITY;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0 ? isFuel(stack) : acceptsMetal(stack);
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            if (resource.isEmpty()) {
                return 0;
            }
            if (isFuel(resource)) {
                if (!fuel.isEmpty() && !FluidStack.isSameFluidSameComponents(fuel, resource)) {
                    return 0;
                }
                int accepted = Math.min(resource.getAmount(), FUEL_CAPACITY - fuel.getAmount());
                if (accepted > 0 && action.execute()) {
                    if (fuel.isEmpty()) {
                        fuel = resource.copyWithAmount(accepted);
                    } else {
                        fuel.grow(accepted);
                    }
                    changed();
                }
                return Math.max(0, accepted);
            }
            if (!acceptsMetal(resource)) {
                return 0;
            }
            int accepted = Math.min(resource.getAmount(), METAL_CAPACITY - metalAmount());
            if (accepted > 0 && action.execute()) {
                addMetal(resource.copyWithAmount(accepted));
                changed();
            }
            return Math.max(0, accepted);
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            for (int i = 0; i < metals.size(); i++) {
                if (FluidStack.isSameFluidSameComponents(metals.get(i), resource)) {
                    return drainMetal(i, resource.getAmount(), action);
                }
            }
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            return metals.isEmpty() ? FluidStack.EMPTY : drainMetal(pourIndex(), maxDrain, action);
        }
    };

    public SmelteryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMELTERY.get(), pos, state);
    }

    @Nullable
    public static CrucibleMeltingRecipe meltingRecipe(Level level, ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return Stream.concat(level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.SMELTERY_MELTING_TYPE.get()).stream(),
                        level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.CRUCIBLE_MELTING_TYPE.get()).stream())
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.ingredients().size() == 1 && recipe.ingredients().getFirst().test(stack))
                .findFirst().orElse(null);
    }

    public static List<RecipeHolder<SmelteryAlloyingRecipe>> alloyingRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.SMELTERY_ALLOYING_TYPE.get());
    }

    public static boolean isAdditive(Level level, ItemStack stack) {
        return alloyingRecipes(level).stream().anyMatch(holder -> holder.value().usesItem(stack));
    }

    public static boolean isFuel(FluidStack stack) {
        return stack.is(FUELS);
    }

    public static int meltTime(CrucibleMeltingRecipe recipe) {
        return Math.max(20, recipe.time() / 3);
    }

    public static int fuelCost(FluidStack result) {
        return Math.max(1, result.getAmount() / 10);
    }

    private boolean acceptsMetal(FluidStack stack) {
        if (this.level == null || isFuel(stack)) {
            return false;
        }
        if (Casting.castingRecipeExists(this.level, stack)) {
            return true;
        }
        for (RecipeHolder<SmelteryAlloyingRecipe> holder : alloyingRecipes(this.level)) {
            if (holder.value().fluids().stream().anyMatch(ingredient -> ingredient.ingredient().test(stack))) {
                return true;
            }
        }
        return false;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SmelteryBlockEntity smeltery) {
        smeltery.tick(level, pos);
    }

    private void tick(Level level, BlockPos pos) {
        boolean dirty = false;
        boolean working = false;
        for (int slot = 0; slot < INPUTS; slot++) {
            ItemStack stack = this.items.getStackInSlot(slot);
            CrucibleMeltingRecipe recipe = meltingRecipe(level, stack);
            if (recipe == null) {
                this.progress[slot] = 0;
                this.total[slot] = 0;
                continue;
            }
            FluidStack result = recipe.result();
            int cost = fuelCost(result);
            this.total[slot] = meltTime(recipe);
            if (this.fuel.getAmount() < cost || metalAmount() + result.getAmount() > METAL_CAPACITY) {
                continue;
            }
            working = true;
            if (++this.progress[slot] < this.total[slot]) {
                continue;
            }
            this.items.setStackInSlot(slot, ItemStack.EMPTY);
            this.progress[slot] = 0;
            addMetal(result.copy());
            this.fuel.shrink(cost);
            if (this.fuel.isEmpty()) {
                this.fuel = FluidStack.EMPTY;
            }
            level.playSound(null, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.5F, 0.8F + level.random.nextFloat() * 0.3F);
            dirty = true;
        }
        if (level.getGameTime() % ALLOY_INTERVAL == 0 && alloy(level)) {
            dirty = true;
        }
        if (working != this.active) {
            this.active = working;
            SmelteryBlock.setLit(level, pos, getBlockState(), working);
            dirty = true;
        }
        if (dirty) {
            changed();
        }
    }

    private boolean alloy(Level level) {
        List<RecipeHolder<SmelteryAlloyingRecipe>> recipes = new ArrayList<>(alloyingRecipes(level));
        recipes.sort(Comparator.comparingInt((RecipeHolder<SmelteryAlloyingRecipe> holder) -> -inputCount(holder.value()))
                .thenComparing(holder -> holder.id().toString()));
        boolean changed = false;
        for (RecipeHolder<SmelteryAlloyingRecipe> holder : recipes) {
            SmelteryAlloyingRecipe recipe = holder.value();
            int[] tanks = findMetals(recipe);
            if (tanks == null || preempted(level, recipe, tanks, recipes)) {
                continue;
            }
            changed |= alloyWhole(recipe, tanks);
        }
        return changed;
    }

    private static int inputCount(SmelteryAlloyingRecipe recipe) {
        return recipe.fluids().size() + recipe.items().size();
    }

    private int @Nullable [] findMetals(SmelteryAlloyingRecipe recipe) {
        int[] tanks = new int[recipe.fluids().size()];
        for (int i = 0; i < tanks.length; i++) {
            tanks[i] = findMetal(recipe.fluids().get(i));
            if (tanks[i] < 0) {
                return null;
            }
        }
        return tanks;
    }

    private boolean preempted(Level level, SmelteryAlloyingRecipe recipe, int[] tanks, List<RecipeHolder<SmelteryAlloyingRecipe>> recipes) {
        List<ItemStack> inputs = inputs();
        int[] slots = recipe.matchItems(inputs);
        if (slots == null) {
            return false;
        }
        for (RecipeHolder<SmelteryAlloyingRecipe> holder : recipes) {
            SmelteryAlloyingRecipe other = holder.value();
            if (other != recipe && inputCount(other) > inputCount(recipe)
                    && covers(other, tanks, slots, inputs) && staged(level, other, inputs)) {
                return true;
            }
        }
        return false;
    }

    private boolean covers(SmelteryAlloyingRecipe other, int[] tanks, int[] slots, List<ItemStack> inputs) {
        for (int tank : tanks) {
            FluidStack metal = this.metals.get(tank);
            if (other.fluids().stream().noneMatch(ingredient -> ingredient.ingredient().test(metal))) {
                return false;
            }
        }
        for (int slot : slots) {
            ItemStack stack = inputs.get(slot);
            if (other.items().stream().noneMatch(ingredient -> ingredient.test(stack))) {
                return false;
            }
        }
        return true;
    }

    private boolean staged(Level level, SmelteryAlloyingRecipe other, List<ItemStack> inputs) {
        if (other.matchItems(inputs) == null) {
            return false;
        }
        for (SizedFluidIngredient ingredient : other.fluids()) {
            if (findMetal(ingredient) < 0 && inputs.stream().noneMatch(stack -> meltsInto(level, stack, ingredient))) {
                return false;
            }
        }
        return true;
    }

    private static boolean meltsInto(Level level, ItemStack stack, SizedFluidIngredient ingredient) {
        CrucibleMeltingRecipe melting = meltingRecipe(level, stack);
        return melting != null && ingredient.ingredient().test(melting.result());
    }

    private boolean alloyWhole(SmelteryAlloyingRecipe recipe, int[] tanks) {
        int[] slots = recipe.matchItems(inputs());
        if (slots == null) {
            return false;
        }
        int consumed = 0;
        for (int i = 0; i < tanks.length; i++) {
            int amount = recipe.fluids().get(i).amount();
            if (this.metals.get(tanks[i]).getAmount() < amount) {
                return false;
            }
            consumed += amount;
        }
        if (metalAmount() - consumed + recipe.result().getAmount() > METAL_CAPACITY) {
            return false;
        }
        consume(recipe, tanks, 1, 1);
        for (int slot : slots) {
            this.items.setStackInSlot(slot, ItemStack.EMPTY);
        }
        addMetal(recipe.result().copy());
        return true;
    }

    private void consume(SmelteryAlloyingRecipe recipe, int[] tanks, int steps, int divisor) {
        for (int i = 0; i < tanks.length; i++) {
            this.metals.get(tanks[i]).shrink(recipe.fluids().get(i).amount() / divisor * steps);
        }
        this.metals.removeIf(FluidStack::isEmpty);
    }

    private int findMetal(SizedFluidIngredient ingredient) {
        for (int i = 0; i < this.metals.size(); i++) {
            if (ingredient.ingredient().test(this.metals.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private void addMetal(FluidStack stack) {
        for (FluidStack metal : this.metals) {
            if (FluidStack.isSameFluidSameComponents(metal, stack)) {
                metal.grow(stack.getAmount());
                return;
            }
        }
        this.metals.add(stack);
    }

    private FluidStack drainMetal(int index, int maxDrain, IFluidHandler.FluidAction action) {
        FluidStack metal = this.metals.get(index);
        int amount = Math.min(maxDrain, metal.getAmount());
        if (amount <= 0) {
            return FluidStack.EMPTY;
        }
        FluidStack drained = metal.copyWithAmount(amount);
        if (action.execute()) {
            metal.shrink(amount);
            if (metal.isEmpty()) {
                this.metals.remove(index);
            }
            changed();
        }
        return drained;
    }

    private int pourIndex() {
        for (int i = 0; i < this.metals.size(); i++) {
            if (this.metals.get(i).getAmount() >= POUR_UNIT) {
                return i;
            }
        }
        return 0;
    }

    public void dumpMetal(int index) {
        if (index >= 0 && index < this.metals.size()) {
            this.metals.remove(index);
            changed();
        }
    }

    public void selectMetal(int index) {
        if (index > 0 && index < this.metals.size()) {
            this.metals.addFirst(this.metals.remove(index));
            changed();
        }
    }

    public void setContents(FluidStack fuel, List<FluidStack> metals, boolean active) {
        this.fuel = fuel.copy();
        this.metals.clear();
        metals.forEach(metal -> this.metals.add(metal.copy()));
        this.active = active;
        changed();
    }

    public int metalAmount() {
        int amount = 0;
        for (FluidStack metal : this.metals) {
            amount += metal.getAmount();
        }
        return amount;
    }

    public List<FluidStack> getMetals() {
        return this.metals;
    }

    public FluidStack getFuel() {
        return this.fuel;
    }

    public boolean isActive() {
        return this.active;
    }

    private List<ItemStack> inputs() {
        List<ItemStack> list = new ArrayList<>(INPUTS);
        for (int i = 0; i < INPUTS; i++) {
            list.add(this.items.getStackInSlot(i));
        }
        return list;
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
        list.addAll(inputs());
        return list;
    }

    private void changed() {
        setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.spicedcider.smeltery");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new SmelteryMenu(id, inventory, this, this.data);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            ItemStackHandler saved = new ItemStackHandler(INPUTS);
            saved.deserializeNBT(registries, tag.getCompound("Inventory"));
            for (int i = 0; i < INPUTS; i++) {
                this.items.setStackInSlot(i, i < saved.getSlots() ? saved.getStackInSlot(i) : ItemStack.EMPTY);
            }
        }
        this.fuel = tag.contains("Fuel") ? FluidStack.parseOptional(registries, tag.getCompound("Fuel")) : FluidStack.EMPTY;
        this.metals.clear();
        ListTag list = tag.getList("Metals", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            FluidStack metal = FluidStack.parseOptional(registries, list.getCompound(i));
            if (!metal.isEmpty()) {
                this.metals.add(metal);
            }
        }
        int[] saved = tag.getIntArray("Progress");
        for (int i = 0; i < INPUTS; i++) {
            this.progress[i] = i < saved.length ? saved[i] : 0;
        }
        this.active = tag.getBoolean("Active");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", this.items.serializeNBT(registries));
        if (!this.fuel.isEmpty()) {
            tag.put("Fuel", this.fuel.save(registries));
        }
        ListTag list = new ListTag();
        for (FluidStack metal : this.metals) {
            list.add(metal.save(registries));
        }
        tag.put("Metals", list);
        tag.putIntArray("Progress", this.progress);
        tag.putBoolean("Active", this.active);
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
