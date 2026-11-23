package com.evandev.spicedcider.recipe;

import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.evandev.spicedcider.registry.ModTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record DryingRecipe(Ingredient ingredient, int count, Optional<Ingredient> agent, ItemStack result, int time,
                           Rack rack) implements Recipe<SingleRecipeInput> {

    public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(DryingRecipe::ingredient),
            Codec.intRange(1, 64).optionalFieldOf("count", 1).forGetter(DryingRecipe::count),
            Ingredient.CODEC_NONEMPTY.optionalFieldOf("agent").forGetter(DryingRecipe::agent),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(DryingRecipe::result),
            Codec.intRange(1, 72000).optionalFieldOf("time", 1200).forGetter(DryingRecipe::time),
            Rack.CODEC.fieldOf("rack").forGetter(DryingRecipe::rack)
    ).apply(instance, DryingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    public static final RecipeSerializer<DryingRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public MapCodec<DryingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    public record Rack(ResourceLocation hung, ResourceLocation drying, ResourceLocation finished, int width, int height) {
        public static final Codec<Rack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("hung").forGetter(Rack::hung),
                ResourceLocation.CODEC.fieldOf("drying").forGetter(Rack::drying),
                ResourceLocation.CODEC.fieldOf("finished").forGetter(Rack::finished),
                Codec.intRange(1, 14).optionalFieldOf("width", 12).forGetter(Rack::width),
                Codec.intRange(1, 28).optionalFieldOf("height", 18).forGetter(Rack::height)
        ).apply(instance, Rack::new));
    }

    public Ingredient agentOrDefault() {
        return agent.orElseGet(() -> Ingredient.of(ModTags.Items.TANNING_AGENTS));
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.DRYING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializers.DRYING_TYPE.get();
    }
}
