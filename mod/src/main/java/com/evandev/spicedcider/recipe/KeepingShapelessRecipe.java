package com.evandev.spicedcider.recipe;

import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class KeepingShapelessRecipe extends ShapelessRecipe {
    private static final MapCodec<KeepingShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RecipeSerializer.SHAPELESS_RECIPE.codec().forGetter(recipe -> recipe),
            Ingredient.CODEC_NONEMPTY.fieldOf("keep").forGetter(recipe -> recipe.keep)
    ).apply(instance, KeepingShapelessRecipe::new));
    private static final StreamCodec<RegistryFriendlyByteBuf, KeepingShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializer.SHAPELESS_RECIPE.streamCodec(), recipe -> recipe,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.keep,
            KeepingShapelessRecipe::new);
    public static final RecipeSerializer<KeepingShapelessRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public @NotNull MapCodec<KeepingShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, KeepingShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    private final Ingredient keep;

    public KeepingShapelessRecipe(ShapelessRecipe recipe, Ingredient keep) {
        super(recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
        this.keep = keep;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        NonNullList<ItemStack> remaining = super.getRemainingItems(input);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && this.keep.test(stack)) {
                remaining.set(i, stack.copyWithCount(1));
            }
        }
        return remaining;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.KEEPING_SHAPELESS.get();
    }
}
