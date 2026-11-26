package com.evandev.spicedcider.compat.supplementaries;

import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacement;
import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacementsAPI;
import net.mehvahdjukaar.supplementaries.configs.CommonConfigs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PrismarineRods {
    public static final ResourceLocation ROD_ITEM = ResourceLocation.fromNamespaceAndPath("upgrade_aquatic", "prismarine_rod");

    private static DeferredBlock<Block> block;

    public static void register(DeferredRegister.Blocks blocks) {
        block = blocks.register("prismarine_rod", () -> new PrismarineRodBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.DIAMOND).strength(0.25F, 0.0F).sound(SoundType.STONE).noOcclusion()));
        AdditionalItemPlacementsAPI.addRegistration(event -> {
            if (CommonConfigs.Tweaks.PLACEABLE_RODS.get()) {
                event.register(BuiltInRegistries.ITEM.get(ROD_ITEM), new AdditionalItemPlacement(block.get()));
            }
        });
    }
}
