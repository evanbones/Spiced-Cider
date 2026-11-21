package com.evandev.spicedcider.content.casting.smeltery;

import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public record SmelteryAlloyingRecipe(List<SizedFluidIngredient> fluids, List<Ingredient> items,
                                     FluidStack result) implements Recipe<SmelteryAlloyingRecipe.Input> {

    public static final MapCodec<SmelteryAlloyingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedFluidIngredient.FLAT_CODEC.listOf(1, 4).fieldOf("fluids").forGetter(SmelteryAlloyingRecipe::fluids),
            Ingredient.CODEC_NONEMPTY.listOf(0, SmelteryBlockEntity.INPUTS).optionalFieldOf("items", List.of()).forGetter(SmelteryAlloyingRecipe::items),
            FluidStack.CODEC.fieldOf("result").forGetter(SmelteryAlloyingRecipe::result)
    ).apply(instance, SmelteryAlloyingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SmelteryAlloyingRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    public static final RecipeSerializer<SmelteryAlloyingRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public MapCodec<SmelteryAlloyingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmelteryAlloyingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    public record Input() implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 0;
        }
    }

    public boolean usesItem(ItemStack stack) {
        return this.items.stream().anyMatch(ingredient -> ingredient.test(stack));
    }

    public int[] matchItems(List<ItemStack> stacks) {
        int[] assigned = new int[this.items.size()];
        boolean[] taken = new boolean[stacks.size()];
        return assign(0, stacks, assigned, taken) ? assigned : null;
    }

    private boolean assign(int index, List<ItemStack> stacks, int[] assigned, boolean[] taken) {
        if (index == this.items.size()) {
            return true;
        }
        for (int slot = 0; slot < stacks.size(); slot++) {
            if (!taken[slot] && !stacks.get(slot).isEmpty() && this.items.get(index).test(stacks.get(slot))) {
                taken[slot] = true;
                assigned[index] = slot;
                if (assign(index + 1, stacks, assigned, taken)) {
                    return true;
                }
                taken[slot] = false;
            }
        }
        return false;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
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
        return NonNullList.of(Ingredient.EMPTY, this.items.toArray(Ingredient[]::new));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SMELTERY_ALLOYING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializers.SMELTERY_ALLOYING_TYPE.get();
    }
}
