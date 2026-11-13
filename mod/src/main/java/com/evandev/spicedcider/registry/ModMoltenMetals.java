package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.content.molten.MoltenFluid;
import com.evandev.spicedcider.content.molten.MoltenFluidType;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModMoltenMetals {
    public static final List<Metal> METALS = List.of(
            new Metal("copper", "minecraft", "minecraft:copper_block", false, MapColor.COLOR_ORANGE, 0xE0612A),
            new Metal("tin", "caverns_and_chasms", "caverns_and_chasms:tin_block", false, MapColor.TERRACOTTA_WHITE, 0xD9A55A),
            new Metal("lead", "modestmining", "modestmining:lead_block", false, MapColor.COLOR_PURPLE, 0xB0605E),
            new Metal("gold", "minecraft", "minecraft:gold_block", false, MapColor.GOLD, 0xF2A21C),
            new Metal("silver", "caverns_and_chasms", "caverns_and_chasms:silver_block", false, MapColor.METAL, 0xE6B07A),
            new Metal("bronze", "alloyed", "alloyed:bronze_block", false, MapColor.COLOR_BROWN, 0xD07E2C),
            new Metal("rose_gold", "oooh_pinky", "oooh_pinky:rose_gold_block", false, MapColor.COLOR_PINK, 0xE8604E),
            new Metal("electrum", "modestmining", "modestmining:electrum_block", false, MapColor.SAND, 0xE8B04A),
            new Metal("iron", "minecraft", "minecraft:iron_block", false, MapColor.METAL, 0xE58A3C),
            new Metal("cast_iron", "minecraft", "spicedcider:cast_iron_block", false, MapColor.COLOR_GRAY, 0xC9582A),
            new Metal("steel", "alloyed", "alloyed:steel_block", false, MapColor.COLOR_LIGHT_GRAY, 0xDA7C40),
            new Metal("netherite", "minecraft", "minecraft:netherite_block", true, MapColor.COLOR_RED, 0xD8342A),
            new Metal("necromium", "caverns_and_chasms", "caverns_and_chasms:necromium_block", true, MapColor.COLOR_GREEN, 0x9AB84E),
            new Metal("prismarite", "modestmining", "modestmining:prismarite_block", false, MapColor.DIAMOND, 0x9AE0C8),
            new Metal("valkyrium", "modestmining", "modestmining:valkyrium_block", false, MapColor.TERRACOTTA_WHITE, 0xF0C090)
    );

    public static final Map<String, Entry> ENTRIES = new LinkedHashMap<>();

    public static void register() {
        for (Metal metal : METALS) {
            if (!metal.mod().equals("minecraft") && !ModList.get().isLoaded(metal.mod())) continue;
            ENTRIES.put(metal.name(), new Entry(metal));
        }
    }

    public static void registerInteractions() {
        DispenseItemBehavior bucketBehavior = DispenserBlock.DISPENSER_REGISTRY.get(Items.LAVA_BUCKET);
        if (bucketBehavior != null) {
            for (Item bucket : List.of(ModItems.LATEX_BUCKET.get(), ModItems.RESIN_BUCKET.get(), ModItems.MAPLE_SYRUP_BUCKET.get(), ModItems.RESIN_OIL_BUCKET.get())) {
                DispenserBlock.registerBehavior(bucket, bucketBehavior);
            }
        }
        for (Entry entry : ENTRIES.values()) {
            if (bucketBehavior != null) DispenserBlock.registerBehavior(entry.bucket().get(), bucketBehavior);
            Block hardened = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(entry.metal().hardensInto()));
            Block block = hardened == Blocks.AIR ? Blocks.STONE : hardened;
            FluidInteractionRegistry.addInteraction(entry.type().get(), new FluidInteractionRegistry.InteractionInformation(
                    NeoForgeMod.WATER_TYPE.value(),
                    state -> state.isSource() ? block.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState()));
        }
    }

    public record Metal(String name, String mod, String hardensInto, boolean fireResistant, MapColor mapColor, int fogColor) {
    }

    public static final class Entry {
        private final Metal metal;
        private final DeferredHolder<FluidType, FluidType> type;
        private final DeferredHolder<Fluid, MoltenFluid.Source> source;
        private final DeferredHolder<Fluid, MoltenFluid.Flowing> flowing;
        private final DeferredBlock<LiquidBlock> block;
        private final DeferredItem<Item> bucket;

        private Entry(Metal metal) {
            this.metal = metal;
            String id = "molten_" + metal.name();
            this.type = ModFluidTypes.FLUID_TYPES.register(id, MoltenFluidType::new);
            this.source = ModFluids.FLUIDS.register(id, () -> new MoltenFluid.Source(properties()));
            this.flowing = ModFluids.FLUIDS.register("flowing_" + id, () -> new MoltenFluid.Flowing(properties()));
            this.block = ModBlocks.BLOCKS.register(id, () -> new LiquidBlock(source.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA)
                    .mapColor(metal.mapColor()).pushReaction(PushReaction.DESTROY)));
            this.bucket = ModItems.ITEMS.register(id + "_bucket", () -> {
                Item.Properties props = new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1);
                return new BucketItem(source.get(), metal.fireResistant() ? props.fireResistant() : props);
            });
        }

        private BaseFlowingFluid.Properties properties() {
            return new BaseFlowingFluid.Properties(type, source, flowing)
                    .slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(30).explosionResistance(100.0F)
                    .bucket(bucket).block(block);
        }

        public Metal metal() {
            return metal;
        }

        public DeferredHolder<FluidType, FluidType> type() {
            return type;
        }

        public DeferredHolder<Fluid, MoltenFluid.Source> source() {
            return source;
        }

        public DeferredHolder<Fluid, MoltenFluid.Flowing> flowing() {
            return flowing;
        }

        public DeferredBlock<LiquidBlock> block() {
            return block;
        }

        public DeferredItem<Item> bucket() {
            return bucket;
        }
    }
}
