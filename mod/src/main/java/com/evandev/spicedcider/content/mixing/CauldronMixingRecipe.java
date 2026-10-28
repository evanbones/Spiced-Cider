package com.evandev.spicedcider.content.mixing;

import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.Optional;

public record CauldronMixingRecipe(Fluid fluid, List<Ingredient> ingredients, boolean heated, boolean keepFluid,
                                   ItemStack result, Optional<Fluid> resultFluid) implements Recipe<CauldronMixingRecipe.Input> {

    public static final MapCodec<CauldronMixingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().optionalFieldOf("fluid", Fluids.EMPTY).forGetter(CauldronMixingRecipe::fluid),
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(CauldronMixingRecipe::ingredients),
            Codec.BOOL.optionalFieldOf("heated", false).forGetter(CauldronMixingRecipe::heated),
            Codec.BOOL.optionalFieldOf("keep_fluid", false).forGetter(CauldronMixingRecipe::keepFluid),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(CauldronMixingRecipe::result),
            BuiltInRegistries.FLUID.byNameCodec().optionalFieldOf("result_fluid").forGetter(CauldronMixingRecipe::resultFluid)
    ).apply(instance, CauldronMixingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CauldronMixingRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    public static final RecipeSerializer<CauldronMixingRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public MapCodec<CauldronMixingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronMixingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    public record Input(List<ItemStack> items) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return this.items.get(index);
        }

        @Override
        public int size() {
            return this.items.size();
        }
    }

    public int[] match(List<ItemStack> units) {
        int[] assigned = new int[this.ingredients.size()];
        boolean[] taken = new boolean[units.size()];
        return this.assign(0, units, assigned, taken) ? assigned : null;
    }

    private boolean assign(int index, List<ItemStack> units, int[] assigned, boolean[] taken) {
        if (index == this.ingredients.size()) {
            return true;
        }
        for (int unit = 0; unit < units.size(); unit++) {
            if (!taken[unit] && this.ingredients.get(index).test(units.get(unit))) {
                taken[unit] = true;
                assigned[index] = unit;
                if (this.assign(index + 1, units, assigned, taken)) {
                    return true;
                }
                taken[unit] = false;
            }
        }
        return false;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return this.match(input.items()) != null;
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.ingredients.toArray(Ingredient[]::new));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CAULDRON_MIXING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializers.CAULDRON_MIXING_TYPE.get();
    }
}
