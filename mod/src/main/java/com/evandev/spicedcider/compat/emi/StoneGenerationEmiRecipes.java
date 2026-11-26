package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.content.stonegen.StoneGenerationRecipe;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class StoneGenerationEmiRecipes {

    public static void register(EmiRegistry registry) {
        EmiStack lava = EmiStack.of(Fluids.LAVA, 1000);
        for (RecipeHolder<StoneGenerationRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.STONE_GENERATION_TYPE.get())) {
            StoneGenerationRecipe recipe = holder.value();
            EmiStack other = recipe.fluid().map(fluid -> EmiStack.of(fluid, 1000))
                    .or(() -> recipe.block().map(block -> EmiStack.of(block.asItem())))
                    .orElse(EmiStack.EMPTY);
            ItemStack result = new ItemStack(recipe.result());
            if (other.isEmpty() || result.isEmpty()) continue;
            ResourceLocation id = holder.id();
            EmiWorldInteractionRecipe.Builder builder = EmiWorldInteractionRecipe.builder()
                    .id(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "/world/stone_generation/" + id.getPath()))
                    .leftInput(lava);
            if (recipe.fluid().isPresent()) {
                builder.rightInput(other, false);
            } else {
                builder.rightInput(other, false, slot -> slot.appendTooltip(Component.translatable("tooltip.emi.fluid_interaction.basalt.blue_ice").withStyle(ChatFormatting.GREEN)));
            }
            recipe.catalyst().ifPresent(set -> {
                List<EmiStack> stacks = set.stream().map(block -> EmiStack.of(block.value().asItem())).filter(stack -> !stack.isEmpty()).toList();
                if (!stacks.isEmpty()) {
                    builder.rightInput(EmiIngredient.of(stacks), true, slot -> slot.appendTooltip(Component.translatable("emi.spicedcider.stone_generation.below").withStyle(ChatFormatting.GREEN)));
                }
            });
            if (recipe.maxY().isPresent() || recipe.minY().isPresent()) {
                Component depth = recipe.maxY().map(y -> Component.translatable("emi.spicedcider.stone_generation.below_y", y))
                        .orElseGet(() -> Component.translatable("emi.spicedcider.stone_generation.above_y", recipe.minY().get()))
                        .withStyle(ChatFormatting.GREEN);
                builder.output(EmiStack.of(result), slot -> slot.appendTooltip(depth));
            } else {
                builder.output(EmiStack.of(result));
            }
            registry.addRecipe(builder.build());
        }
    }
}
