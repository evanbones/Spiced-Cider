package com.evandev.spicedcider.recipe;

import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AnvilSmithingRecipe(Ingredient base, Ingredient addition, ItemStack result) implements Recipe<SmithingRecipeInput> {

    public static final MapCodec<AnvilSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(AnvilSmithingRecipe::base),
            Ingredient.CODEC_NONEMPTY.fieldOf("addition").forGetter(AnvilSmithingRecipe::addition),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(AnvilSmithingRecipe::result)
    ).apply(instance, AnvilSmithingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnvilSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, AnvilSmithingRecipe::base,
            Ingredient.CONTENTS_STREAM_CODEC, AnvilSmithingRecipe::addition,
            ItemStack.STREAM_CODEC, AnvilSmithingRecipe::result,
            AnvilSmithingRecipe::new
    );

    public static final RecipeSerializer<AnvilSmithingRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public @NotNull MapCodec<AnvilSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, AnvilSmithingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    @Override
    public boolean matches(SmithingRecipeInput input, @NotNull Level level) {
        return this.base.test(input.base()) && this.addition.test(input.addition());
    }

    @Override
    public @NotNull ItemStack assemble(SmithingRecipeInput input, HolderLookup.@NotNull Provider registries) {
        ItemStack stack = input.base().transmuteCopy(this.result.getItem(), this.result.getCount());
        stack.applyComponents(this.result.getComponentsPatch());
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return this.result;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.base, this.addition);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ANVIL_SMITHING.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipeSerializers.ANVIL_SMITHING_TYPE.get();
    }
}
