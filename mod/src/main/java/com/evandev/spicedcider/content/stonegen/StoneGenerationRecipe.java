package com.evandev.spicedcider.content.stonegen;

import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

public record StoneGenerationRecipe(ResourceLocation with, Optional<HolderSet<Block>> catalyst, Optional<Integer> minY, Optional<Integer> maxY, Block result) implements Recipe<StoneGenerationRecipe.Input> {

    public static final MapCodec<StoneGenerationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("with", ResourceLocation.withDefaultNamespace("water")).forGetter(StoneGenerationRecipe::with),
            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("catalyst").forGetter(StoneGenerationRecipe::catalyst),
            Codec.INT.optionalFieldOf("min_y").forGetter(StoneGenerationRecipe::minY),
            Codec.INT.optionalFieldOf("max_y").forGetter(StoneGenerationRecipe::maxY),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("result").forGetter(StoneGenerationRecipe::result)
    ).apply(instance, StoneGenerationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StoneGenerationRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    public static final RecipeSerializer<StoneGenerationRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public MapCodec<StoneGenerationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StoneGenerationRecipe> streamCodec() {
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

    public Optional<Fluid> fluid() {
        Fluid fluid = BuiltInRegistries.FLUID.get(this.with);
        return fluid == Fluids.EMPTY ? Optional.empty() : Optional.of(fluid);
    }

    public Optional<Block> block() {
        if (fluid().isPresent()) return Optional.empty();
        Block block = BuiltInRegistries.BLOCK.get(this.with);
        return block == Blocks.AIR ? Optional.empty() : Optional.of(block);
    }

    public boolean matchesSpot(BlockState below, BlockPos pos) {
        if (this.catalyst.isPresent() && !below.is(this.catalyst.get())) return false;
        if (this.minY.isPresent() && pos.getY() < this.minY.get()) return false;
        return this.maxY.isEmpty() || pos.getY() < this.maxY.get();
    }

    @Override
    public boolean matches(Input input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return new ItemStack(this.result);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(this.result);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.STONE_GENERATION.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeSerializers.STONE_GENERATION_TYPE.get();
    }
}
