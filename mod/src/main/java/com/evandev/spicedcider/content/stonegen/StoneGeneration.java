package com.evandev.spicedcider.content.stonegen;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class StoneGeneration {

    public static void registerInteractions() {
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                (level, currentPos, relativePos, currentState) -> findBlockResult(level, currentPos, level.getBlockState(relativePos)).isPresent(),
                (level, currentPos, relativePos, currentState) -> findBlockResult(level, currentPos, level.getBlockState(relativePos)).ifPresent(state -> {
                    level.setBlockAndUpdate(currentPos, EventHooks.fireFluidPlaceBlockEvent(level, currentPos, currentPos, state));
                    level.levelEvent(1501, currentPos, 0);
                })));
    }

    @SubscribeEvent
    public static void onFluidPlace(BlockEvent.FluidPlaceBlockEvent event) {
        if (!(event.getLevel() instanceof Level level)) return;
        BlockState placed = event.getNewState();
        BlockState original = event.getOriginalState();
        boolean flowingMeet = placed.is(Blocks.COBBLESTONE) && original.getFluidState().is(FluidTags.LAVA);
        boolean fromAbove = placed.is(Blocks.STONE) && original.getFluidState().is(FluidTags.WATER);
        if (!flowingMeet && !fromAbove) return;
        BlockPos pos = event.getPos();
        BlockState below = level.getBlockState(pos.below());
        sorted(level).stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.fluid().map(fluid -> fluid.isSame(Fluids.WATER)).orElse(false))
                .filter(recipe -> recipe.matchesSpot(below, pos))
                .findFirst()
                .ifPresent(recipe -> event.setNewState(recipe.result().defaultBlockState()));
    }

    private static Optional<BlockState> findBlockResult(Level level, BlockPos pos, BlockState touching) {
        BlockState below = level.getBlockState(pos.below());
        return sorted(level).stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.block().map(touching::is).orElse(false))
                .filter(recipe -> recipe.matchesSpot(below, pos))
                .findFirst()
                .map(recipe -> recipe.result().defaultBlockState());
    }

    public static List<RecipeHolder<StoneGenerationRecipe>> sorted(Level level) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.STONE_GENERATION_TYPE.get()).stream()
                .sorted(Comparator.comparing((RecipeHolder<StoneGenerationRecipe> holder) -> holder.value().catalyst().isEmpty()))
                .toList();
    }
}
