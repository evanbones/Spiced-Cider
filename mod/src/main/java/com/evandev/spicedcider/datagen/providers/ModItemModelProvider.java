package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SpicedCider.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(ModItems.FLINT_HAMMER.get());
        handheldItem(ModItems.IRON_HAMMER.get());
        handheldItem(ModItems.GOLDEN_HAMMER.get());
        handheldItem(ModItems.DIAMOND_HAMMER.get());
        handheldItem(ModItems.NETHERITE_HAMMER.get());

        handheldItem(ModItems.FIRE_STRIKER.get());

        basicItem(ModItems.GRAPPLING_HOOK.get());
        basicItem(ModItems.STICKY_GRAPPLING_HOOK.get());

        withExistingParent("rubber_cable", "item/generated")
                .texture("layer0", modLoc("item/cable"));

        basicItem(ModItems.BLAST_PROOF_PLATING.get());
        basicItem(ModItems.MISCHIEF_HELMET.get());
        basicItem(ModItems.MISCHIEF_CHESTPLATE.get());
        basicItem(ModItems.MISCHIEF_LEGGINGS.get());
        basicItem(ModItems.MISCHIEF_BOOTS.get());
    }

    public @NotNull ItemModelBuilder handheldItem(@NotNull Item item) {
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        return withExistingParent(name, "item/handheld")
                .texture("layer0", modLoc("item/" + name));
    }
}