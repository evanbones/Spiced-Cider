package com.evandev.spicedcider.compat.emi;

import com.baisylia.modestmining.item.custom.weapons.JavelinItem;
import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.evandev.spicedcider.util.RemovedRepairItems;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class JavelinRepairEmiRecipes {

    public static void register(EmiRegistry registry) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof JavelinItem javelin) || ReliableRemoverCompat.isRemoved(item)) continue;
            List<ItemStack> materials = RemovedRepairItems.displayItems(javelin.getTier().getRepairIngredient());
            if (materials.isEmpty()) continue;
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("spicedcider", "/anvil/repairing/material/" + key.getNamespace() + "/" + key.getPath());
            registry.addRecipe(new EmiAnvilRecipe(EmiStack.of(item), EmiIngredient.of(materials.stream().map(EmiStack::of).toList()), id));
        }
    }
}
