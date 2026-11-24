package com.evandev.spicedcider.content.casting;

import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public record BasinCastingRecipe(SizedFluidIngredient fluid, ItemStack result, int time) implements Recipe<BasinCastingRecipe.Input> {

    public static final MapCodec<BasinCastingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(BasinCastingRecipe::fluid),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(BasinCastingRecipe::result),
            Codec.intRange(1, 72000).optionalFieldOf("time", 200).forGetter(BasinCastingRecipe::time)
    ).apply(instance, BasinCastingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BasinCastingRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    public static final RecipeSerializer<BasinCastingRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public MapCodec<BasinCastingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BasinCastingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    public record Input(FluidStack fluid) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 0;
        }
    }

    public int amount() {
        return this.fluid.amount();
    }

    public boolean accepts(FluidStack stack) {
        return this.fluid.ingredient().test(stack);
    }

    @Override
    public boolean matches(Input input, Level level) {
        return accepts(input.fluid());
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
        return this.result.copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BASIN_CASTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializers.BASIN_CASTING_TYPE.get();
    }
}
