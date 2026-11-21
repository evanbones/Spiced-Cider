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

public record MouldCastingRecipe(MouldShape shape, SizedFluidIngredient fluid, ItemStack result,
                                 int time) implements Recipe<MouldCastingRecipe.Input> {

    public static final MapCodec<MouldCastingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MouldShape.CODEC.fieldOf("shape").forGetter(MouldCastingRecipe::shape),
            SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(MouldCastingRecipe::fluid),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(MouldCastingRecipe::result),
            Codec.intRange(1, 72000).optionalFieldOf("time", 100).forGetter(MouldCastingRecipe::time)
    ).apply(instance, MouldCastingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MouldCastingRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    public static final RecipeSerializer<MouldCastingRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public MapCodec<MouldCastingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MouldCastingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    public record Input(MouldShape shape, FluidStack fluid) implements RecipeInput {
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

    public boolean accepts(MouldShape shape, FluidStack stack) {
        return this.shape == shape && this.fluid.ingredient().test(stack);
    }

    @Override
    public boolean matches(Input input, Level level) {
        return accepts(input.shape(), input.fluid());
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
        return ModRecipeSerializers.MOULD_CASTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializers.MOULD_CASTING_TYPE.get();
    }
}
