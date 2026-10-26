package com.evandev.spicedcider.recipe;

import com.evandev.spicedcider.content.item.PocketJukeboxItem;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class PocketJukeboxDyeRecipe extends ShapelessRecipe {
    public static final RecipeSerializer<PocketJukeboxDyeRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public @NotNull MapCodec<PocketJukeboxDyeRecipe> codec() {
            return RecipeSerializer.SHAPELESS_RECIPE.codec().xmap(PocketJukeboxDyeRecipe::new, recipe -> recipe);
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, PocketJukeboxDyeRecipe> streamCodec() {
            return RecipeSerializer.SHAPELESS_RECIPE.streamCodec().map(PocketJukeboxDyeRecipe::new, recipe -> recipe);
        }
    };

    public PocketJukeboxDyeRecipe(ShapelessRecipe recipe) {
        super(recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, @NotNull HolderLookup.Provider registries) {
        ItemStack result = super.assemble(input, registries);
        for (ItemStack stack : input.items()) {
            if (stack.getItem() instanceof PocketJukeboxItem) return stack.transmuteCopy(result.getItem(), 1);
        }
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.POCKET_JUKEBOX_DYE.get();
    }
}
