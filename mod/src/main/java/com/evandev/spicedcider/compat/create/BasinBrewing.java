package com.evandev.spicedcider.compat.create;

import com.evandev.spicedcider.content.brewing.Brewing;
import com.evandev.spicedcider.registry.ModDataMaps;
import com.evandev.spicedcider.registry.ModPotions;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BasinBrewing {
    public static final Recipe<RecipeInput> RECIPE = sentinel();
    private static final String STATE = "spicedcider_brew";

    private record Take(int slot, int count) {
    }

    private record Plan(IFluidHandler source, FluidStack before, FluidStack after, List<Take> takes, List<ItemStack> outputs, int drain, boolean tainted) {
    }

    public static final ResourceLocation POTION_FLUID = ResourceLocation.fromNamespaceAndPath("create", "potion");

    public static boolean isPotion(FluidStack stack) {
        return !stack.isEmpty() && POTION_FLUID.equals(BuiltInRegistries.FLUID.getKey(stack.getFluid()));
    }

    public static Recipe<RecipeInput> sentinel() {
        return new BrewRecipe();
    }

    public static boolean canBrew(BasinBlockEntity basin) {
        return plan(basin) != null;
    }

    public static boolean brew(BasinBlockEntity basin) {
        Plan plan = plan(basin);
        if (plan == null) return false;
        IItemHandlerModifiable items = basin.getInputInventory();
        if (!plan.outputs().isEmpty() && !basin.acceptOutputs(plan.outputs(), List.of(), true)) return false;
        for (Take take : plan.takes()) {
            items.extractItem(take.slot(), take.count(), false);
        }
        plan.source().drain(plan.before().copy(), IFluidHandler.FluidAction.EXECUTE);
        FluidStack after = plan.after().copyWithAmount(plan.after().getAmount() - plan.drain());
        if (!after.isEmpty()) basin.inputTank.getCapability().fill(after, IFluidHandler.FluidAction.EXECUTE);
        if (!plan.outputs().isEmpty()) basin.acceptOutputs(plan.outputs(), List.of(), false);
        Level level = basin.getLevel();
        if (plan.tainted() && level instanceof ServerLevel server) {
            BlockPos pos = basin.getBlockPos();
            server.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.8F);
            server.playSound(null, pos, SoundEvents.WITCH_CELEBRATE, SoundSource.BLOCKS, 0.8F, 1.0F);
        }
        basin.notifyChangeOfContents();
        basin.notifyUpdate();
        return true;
    }

    @Nullable
    private static Plan plan(BasinBlockEntity basin) {
        FluidStack fluid = FluidStack.EMPTY;
        IFluidHandler source = null;
        for (SmartFluidTankBehaviour behaviour : List.of(basin.getTanks().getFirst(), basin.getTanks().getSecond())) {
            IFluidHandler tank = behaviour.getCapability();
            for (int i = 0; i < tank.getTanks() && source == null; i++) {
                FluidStack stack = tank.getFluidInTank(i);
                if (isPotion(stack)) {
                    fluid = stack;
                    source = tank;
                }
            }
        }
        if (fluid.isEmpty()) return null;
        PotionContents contents = fluid.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        Optional<Holder<Potion>> potion = contents.potion();
        if (potion.isEmpty() || Brewing.isTainted(potion) || Brewing.isWater(potion)) return null;

        boolean clear = Brewing.isClear(potion);
        Holder<Potion> brew = clear ? null : potion.get();
        CompoundTag state = fluid.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound(STATE);
        int seconds = state.getInt("seconds");
        int potency = state.getInt("potency");
        int count = state.getInt("count");
        BottleType bottle = fluid.getOrDefault(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, BottleType.REGULAR);
        BottleType startBottle = bottle;
        boolean folded = false;
        boolean tainted = false;
        int arrows = 0;
        List<Take> takes = new ArrayList<>();
        IItemHandlerModifiable items = basin.getInputInventory();

        for (int slot = 0; slot < items.getSlots() && !tainted; slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            if (stack.is(Brewing.MAKES_SPLASH) || stack.is(Brewing.MAKES_LINGERING)) {
                BottleType target = stack.is(Brewing.MAKES_SPLASH) ? BottleType.SPLASH : BottleType.LINGERING;
                if (bottle != target) {
                    bottle = target;
                    takes.add(new Take(slot, 1));
                }
                continue;
            }
            if (stack.is(Items.ARROW)) {
                arrows += stack.getCount();
                continue;
            }
            ModDataMaps.BrewingIngredient ingredient = Brewing.ingredient(stack);
            if (ingredient == null) continue;
            int used = 0;
            while (used < stack.getCount() && count < Brewing.MAX_INGREDIENTS) {
                if (ingredient.potion().isPresent()) {
                    Holder<Potion> kind = ingredient.potion().get();
                    if (brew != null && !Brewing.same(brew, kind)) {
                        tainted = true;
                        used++;
                        break;
                    }
                    brew = kind;
                }
                seconds += ingredient.seconds();
                potency += ingredient.potency();
                count++;
                used++;
                folded = true;
            }
            if (used > 0) takes.add(new Take(slot, used));
        }

        if (tainted) {
            PotionContents taint = new PotionContents(Optional.of(ModPotions.TAINTED), Optional.of(Brewing.TAINTED_COLOR), List.of());
            FluidStack after = PotionFluid.of(fluid.getAmount(), taint, bottle);
            return new Plan(source, fluid.copy(), after, takes, List.of(), 0, true);
        }

        FluidStack after = fluid.copy();
        if (folded && brew != null) {
            PotionContents brewed = new PotionContents(Optional.of(brew), Optional.empty(), Brewing.effects(brew, seconds, potency));
            after = PotionFluid.of(fluid.getAmount(), brewed, bottle);
            CompoundTag tag = new CompoundTag();
            tag.putInt("seconds", seconds);
            tag.putInt("potency", potency);
            tag.putInt("count", count);
            CompoundTag root = new CompoundTag();
            root.put(STATE, tag);
            after.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        } else if (bottle != startBottle) {
            after.set(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, bottle);
        }

        List<ItemStack> outputs = new ArrayList<>();
        int drain = 0;
        if (brew != null && arrows > 0) {
            int tipped = Math.min(arrows, after.getAmount() * Brewing.ARROWS_PER_BOTTLE / Brewing.BOTTLE);
            if (tipped > 0) {
                ItemStack arrow = new ItemStack(Items.TIPPED_ARROW, tipped);
                arrow.set(DataComponents.POTION_CONTENTS, after.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
                outputs.add(arrow);
                drain = (tipped * Brewing.BOTTLE + Brewing.ARROWS_PER_BOTTLE - 1) / Brewing.ARROWS_PER_BOTTLE;
                int left = tipped;
                for (int slot = 0; slot < items.getSlots() && left > 0; slot++) {
                    ItemStack stack = items.getStackInSlot(slot);
                    if (!stack.is(Items.ARROW)) continue;
                    int take = Math.min(left, stack.getCount());
                    takes.add(new Take(slot, take));
                    left -= take;
                }
            }
        }

        if (takes.isEmpty()) return null;
        return new Plan(source, fluid.copy(), after, takes, outputs, drain, false);
    }

    private static final class BrewRecipe implements Recipe<RecipeInput> {
        @Override
        public boolean matches(RecipeInput input, Level level) {
            return false;
        }

        @Override
        public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return true;
        }

        @Override
        public ItemStack getResultItem(HolderLookup.Provider registries) {
            return ItemStack.EMPTY;
        }

        @Override
        public NonNullList<Ingredient> getIngredients() {
            return NonNullList.create();
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return RecipeSerializer.SHAPELESS_RECIPE;
        }

        @Override
        public RecipeType<?> getType() {
            return RecipeType.CRAFTING;
        }

        @Override
        public boolean isSpecial() {
            return true;
        }
    }
}
