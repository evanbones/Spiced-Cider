package com.evandev.spicedcider.content.casting;

import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.Codec;
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

import java.util.List;

public record CrucibleMeltingRecipe(List<Ingredient> ingredients, FluidStack result, CastingHeat heat,
                                    int time, boolean smeltery) implements Recipe<CrucibleMeltingRecipe.Input> {

    public static final MapCodec<CrucibleMeltingRecipe> CODEC = codec(false);
    public static final MapCodec<CrucibleMeltingRecipe> SMELTERY_CODEC = codec(true);
    public static final RecipeSerializer<CrucibleMeltingRecipe> SERIALIZER = serializer(CODEC);
    public static final RecipeSerializer<CrucibleMeltingRecipe> SMELTERY_SERIALIZER = serializer(SMELTERY_CODEC);

    private static MapCodec<CrucibleMeltingRecipe> codec(boolean smeltery) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf(1, CrucibleBlockEntity.INPUTS).fieldOf("ingredients").forGetter(CrucibleMeltingRecipe::ingredients),
                FluidStack.CODEC.fieldOf("result").forGetter(CrucibleMeltingRecipe::result),
                CastingHeat.CODEC.optionalFieldOf("heat", smeltery ? CastingHeat.HIGH : CastingHeat.LOW).forGetter(CrucibleMeltingRecipe::heat),
                Codec.intRange(1, 72000).optionalFieldOf("time", 200).forGetter(CrucibleMeltingRecipe::time)
        ).apply(instance, (ingredients, result, heat, time) -> new CrucibleMeltingRecipe(ingredients, result, heat, time, smeltery)));
    }

    private static RecipeSerializer<CrucibleMeltingRecipe> serializer(MapCodec<CrucibleMeltingRecipe> codec) {
        StreamCodec<RegistryFriendlyByteBuf, CrucibleMeltingRecipe> stream = ByteBufCodecs.fromCodecWithRegistries(codec.codec());
        return new RecipeSerializer<>() {
            @Override
            public MapCodec<CrucibleMeltingRecipe> codec() {
                return codec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, CrucibleMeltingRecipe> streamCodec() {
                return stream;
            }
        };
    }

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

    public int[] match(List<ItemStack> stacks) {
        return match(stacks, 0);
    }

    public int[] match(List<ItemStack> stacks, int skipped) {
        int[] assigned = new int[this.ingredients.size()];
        boolean[] taken = new boolean[stacks.size()];
        return assign(0, stacks, assigned, taken, skipped) ? assigned : null;
    }

    private boolean assign(int index, List<ItemStack> stacks, int[] assigned, boolean[] taken, int skipped) {
        if (index == this.ingredients.size()) {
            return true;
        }
        if ((skipped & (1 << index)) != 0) {
            assigned[index] = -1;
            return assign(index + 1, stacks, assigned, taken, skipped);
        }
        for (int slot = 0; slot < stacks.size(); slot++) {
            if (!taken[slot] && !stacks.get(slot).isEmpty() && this.ingredients.get(index).test(stacks.get(slot))) {
                taken[slot] = true;
                assigned[index] = slot;
                if (assign(index + 1, stacks, assigned, taken, skipped)) {
                    return true;
                }
                taken[slot] = false;
            }
        }
        return false;
    }

    public boolean uses(ItemStack stack) {
        return this.ingredients.stream().anyMatch(ingredient -> ingredient.test(stack));
    }

    @Override
    public boolean matches(Input input, Level level) {
        return match(input.items()) != null;
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
        return NonNullList.of(Ingredient.EMPTY, this.ingredients.toArray(Ingredient[]::new));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.smeltery ? ModRecipeSerializers.SMELTERY_MELTING.get() : ModRecipeSerializers.CRUCIBLE_MELTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return this.smeltery ? ModRecipeSerializers.SMELTERY_MELTING_TYPE.get() : ModRecipeSerializers.CRUCIBLE_MELTING_TYPE.get();
    }
}
