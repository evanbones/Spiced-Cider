package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;

public final class RepairMaterialTagEmiRecipes {

    public static void register(EmiRegistry registry) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (ReliableRemoverCompat.isRemoved(item)) continue;
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("spicedcider", "repair_materials/" + key.getNamespace() + "/" + key.getPath()));
            List<EmiStack> materials = BuiltInRegistries.ITEM.getTag(tag).stream()
                    .flatMap(set -> set.stream())
                    .filter(holder -> !ReliableRemoverCompat.isRemoved(holder.value()))
                    .map(holder -> EmiStack.of(holder.value()))
                    .toList();
            if (materials.isEmpty()) continue;
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("spicedcider", "/anvil/repairing/tag/" + key.getNamespace() + "/" + key.getPath());
            registry.addRecipe(new EmiAnvilRecipe(EmiStack.of(item), EmiIngredient.of(materials), id));
        }
    }
}
