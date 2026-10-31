package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.etcetera.ChiselEvents;
import com.evandev.spicedcider.compat.etcetera.ChiseledVariants;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

public class ChiselingEmiRecipes {

    public static void register(EmiRegistry registry) {
        EmiStack chisel = EmiStack.of(ChiselEvents.chisel());
        for (Map.Entry<Block, List<Block>> entry : ChiseledVariants.map().entrySet()) {
            ResourceLocation source = BuiltInRegistries.BLOCK.getKey(entry.getKey());
            for (Block target : entry.getValue()) {
                registry.addRecipe(EmiWorldInteractionRecipe.builder()
                        .id(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/chiseling/" + source.getNamespace() + "/" + source.getPath()))
                        .leftInput(EmiStack.of(entry.getKey()))
                        .rightInput(chisel, true)
                        .output(EmiStack.of(target))
                        .build());
            }
        }
    }
}
