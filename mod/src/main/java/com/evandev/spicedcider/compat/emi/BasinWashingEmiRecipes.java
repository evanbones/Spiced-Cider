package com.evandev.spicedcider.compat.emi;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.material.Fluids;

public class BasinWashingEmiRecipes {
    private static final String CAULDRON_WASHING = "/world/cauldron_washing/";

    public static void register(EmiRegistry registry) {
        registry.removeRecipes(recipe -> recipe.getId() != null && recipe.getId().getNamespace().equals("emi") && recipe.getId().getPath().startsWith(CAULDRON_WASHING));
        EmiStack basin = EmiStack.of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "basin")));
        EmiStack water = EmiStack.of(Fluids.WATER, 250);
        for (Item item : BuiltInRegistries.ITEM) {
            if (!item.getDefaultInstance().is(ItemTags.DYEABLE)) continue;
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            int seed = item.hashCode();
            registry.addRecipe(EmiWorldInteractionRecipe.builder()
                    .id(ResourceLocation.fromNamespaceAndPath("spicedcider", "/world/basin_washing/" + key.getNamespace() + "/" + key.getPath()))
                    .leftInput(EmiStack.EMPTY, slot -> new GeneratedSlotWidget(random -> {
                        ItemStack stack = item.getDefaultInstance();
                        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(random.nextInt(0xFFFFFF + 1), true));
                        return EmiStack.of(stack);
                    }, seed, slot.getBounds().x(), slot.getBounds().y()))
                    .rightInput(basin, true)
                    .rightInput(water, false)
                    .output(EmiStack.of(item))
                    .supportsRecipeTree(false)
                    .build());
        }
    }
}
