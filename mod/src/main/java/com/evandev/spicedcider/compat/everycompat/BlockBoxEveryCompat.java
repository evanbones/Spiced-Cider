package com.evandev.spicedcider.compat.everycompat;

import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import vectorwing.blockbox.BlockBox;
import vectorwing.blockbox.common.block.PalisadeBlock;
import vectorwing.blockbox.common.block.SeatBlock;
import vectorwing.blockbox.common.block.SpikedPalisadeBlock;
import vectorwing.blockbox.common.registry.ModBlocks;

public class BlockBoxEveryCompat extends SimpleModule {

    public final SimpleEntrySet<WoodType, Block> seats;
    public final SimpleEntrySet<WoodType, Block> strippedSpikedPalisades;
    public final SimpleEntrySet<WoodType, Block> spikedPalisades;
    public final SimpleEntrySet<WoodType, Block> strippedPalisades;
    public final SimpleEntrySet<WoodType, Block> palisades;

    public BlockBoxEveryCompat(String myModId) {
        super(BlockBox.MODID, "bb", myModId);

        ResourceLocation tabKey = ResourceLocation.parse("minecraft:building_blocks");
        ResourceKey<net.minecraft.world.item.CreativeModeTab> tab = ResourceKey.create(Registries.CREATIVE_MODE_TAB, tabKey);

        ResourceLocation axeTag = ResourceLocation.parse("minecraft:mineable/axe");
        ResourceLocation wallsTag = ResourceLocation.parse("minecraft:walls");

        ResourceLocation seatsTag = ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "wooden_seats");
        ResourceLocation palisadesTag = ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "palisades");
        ResourceLocation spikedPalisadesTag = ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "spiked_palisades");

        ResourceLocation sableLightTag = ResourceLocation.parse("sable:light");
        ResourceLocation sableSuperLightTag = ResourceLocation.parse("sable:super_light");

        seats = SimpleEntrySet.builder(WoodType.class, "seat",
                        ModBlocks.OAK_SEAT, () -> VanillaWoodTypes.OAK,
                        w -> new SeatBlock(Utils.copyPropertySafe(w.planks)))
                .setTabKey(tab)
                .addTag(seatsTag, Registries.BLOCK, Registries.ITEM)
                .addTag(axeTag, Registries.BLOCK)
                .addTag(sableLightTag, Registries.BLOCK)
                .defaultRecipe()
                .useMergedPalette()
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/oak_seat_top"))
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/oak_seat_side"))
                .build();

        strippedSpikedPalisades = SimpleEntrySet.builder(WoodType.class, "palisade", "stripped_spiked",
                        ModBlocks.STRIPPED_SPIKED_OAK_PALISADE, () -> VanillaWoodTypes.OAK,
                        w -> new SpikedPalisadeBlock(null, Utils.copyPropertySafe(w.planks)))
                .setTabKey(tab)
                .addTag(spikedPalisadesTag, Registries.BLOCK, Registries.ITEM)
                .addTag(wallsTag, Registries.BLOCK)
                .addTag(axeTag, Registries.BLOCK)
                .addTag(sableSuperLightTag, Registries.BLOCK)
                .defaultRecipe()
                .useMergedPalette()
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/stripped_oak_palisade_top"))
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/stripped_oak_palisade_side"))
                .build();

        spikedPalisades = SimpleEntrySet.builder(WoodType.class, "palisade", "spiked",
                        ModBlocks.SPIKED_OAK_PALISADE, () -> VanillaWoodTypes.OAK,
                        w -> new SpikedPalisadeBlock(
                                () -> strippedSpikedPalisades.blocks.get(w),
                                Utils.copyPropertySafe(w.planks)))
                .setTabKey(tab)
                .addTag(spikedPalisadesTag, Registries.BLOCK, Registries.ITEM)
                .addTag(wallsTag, Registries.BLOCK)
                .addTag(axeTag, Registries.BLOCK)
                .addTag(sableSuperLightTag, Registries.BLOCK)
                .defaultRecipe()
                .useMergedPalette()
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/oak_palisade_top"))
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/oak_palisade_side"))
                .build();

        strippedPalisades = SimpleEntrySet.builder(WoodType.class, "palisade", "stripped",
                        ModBlocks.STRIPPED_OAK_PALISADE, () -> VanillaWoodTypes.OAK,
                        w -> new PalisadeBlock(
                                () -> strippedSpikedPalisades.blocks.get(w),
                                null,
                                Utils.copyPropertySafe(w.planks)))
                .setTabKey(tab)
                .addTag(palisadesTag, Registries.BLOCK, Registries.ITEM)
                .addTag(wallsTag, Registries.BLOCK)
                .addTag(axeTag, Registries.BLOCK)
                .addTag(sableLightTag, Registries.BLOCK)
                .defaultRecipe()
                .useMergedPalette()
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/stripped_oak_palisade_top"))
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/stripped_oak_palisade_side"))
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/stripped_oak_palisade_core"))
                .build();

        palisades = SimpleEntrySet.builder(WoodType.class, "palisade",
                        ModBlocks.OAK_PALISADE, () -> VanillaWoodTypes.OAK,
                        w -> new PalisadeBlock(
                                () -> spikedPalisades.blocks.get(w),
                                () -> strippedPalisades.blocks.get(w),
                                Utils.copyPropertySafe(w.planks)))
                .setTabKey(tab)
                .addTag(palisadesTag, Registries.BLOCK, Registries.ITEM)
                .addTag(wallsTag, Registries.BLOCK)
                .addTag(axeTag, Registries.BLOCK)
                .addTag(sableLightTag, Registries.BLOCK)
                .defaultRecipe()
                .useMergedPalette()
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/oak_palisade_top"))
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/oak_palisade_side"))
                .addTexture(ResourceLocation.fromNamespaceAndPath(BlockBox.MODID, "block/oak_palisade_core"))
                .build();

        this.addEntry(seats);
        this.addEntry(strippedSpikedPalisades);
        this.addEntry(spikedPalisades);
        this.addEntry(strippedPalisades);
        this.addEntry(palisades);
    }
}