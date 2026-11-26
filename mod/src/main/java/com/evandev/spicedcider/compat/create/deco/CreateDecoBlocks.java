package com.evandev.spicedcider.compat.create.deco;

import com.evandev.spicedcider.registry.ModBlockEntities;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModDecoBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateDecoBlocks {
    public static final Map<DyeColor, DeferredBlock<ShippingContainerBlock>> CONTAINERS = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, DeferredHolder<BlockEntityType<?>, BlockEntityType<ShippingContainerBlock.Entity>>> CONTAINER_ENTITIES = new EnumMap<>(DyeColor.class);
    public static final Map<String, DeferredBlock<CatwalkBlock>> CATWALKS = new LinkedHashMap<>();
    public static final Map<String, DeferredBlock<CatwalkStairBlock>> CATWALK_STAIRS = new LinkedHashMap<>();
    public static final Map<String, DeferredBlock<CatwalkRailingBlock>> CATWALK_RAILINGS = new LinkedHashMap<>();
    public static final List<DeferredItem<? extends BlockItem>> CATWALK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<? extends BlockItem>> CONTAINER_ITEMS = new ArrayList<>();

    public static void register() {
        for (String metal : ModDecoBlocks.CATWALK_METALS) {
            DeferredBlock<CatwalkRailingBlock> railing = ModBlocks.BLOCKS.register(metal + "_catwalk_railing", () -> new CatwalkRailingBlock(catwalkProperties()));
            DeferredBlock<CatwalkBlock> catwalk = ModBlocks.BLOCKS.register(metal + "_catwalk", () -> new CatwalkBlock(catwalkProperties()));
            DeferredBlock<CatwalkStairBlock> stairs = ModBlocks.BLOCKS.register(metal + "_catwalk_stairs", () -> new CatwalkStairBlock(catwalkProperties(), railing));
            CATWALKS.put(metal, catwalk);
            CATWALK_STAIRS.put(metal, stairs);
            CATWALK_RAILINGS.put(metal, railing);
            CATWALK_ITEMS.add(ModItems.ITEMS.register(catwalk.getId().getPath(), () -> new CatwalkItems.Catwalk(catwalk.get(), new Item.Properties())));
            CATWALK_ITEMS.add(ModItems.ITEMS.register(stairs.getId().getPath(), () -> new CatwalkItems.Stairs(stairs.get(), new Item.Properties())));
            CATWALK_ITEMS.add(ModItems.ITEMS.register(railing.getId().getPath(), () -> new CatwalkItems.Railing(railing.get(), new Item.Properties())));
        }
        for (DyeColor color : ModDecoBlocks.CONTAINER_COLORS) {
            String name = color.getName() + "_shipping_container";
            DeferredBlock<ShippingContainerBlock> block = ModBlocks.BLOCKS.register(name, () -> new ShippingContainerBlock(BlockBehaviour.Properties.of()
                    .mapColor(color.getMapColor())
                    .strength(5.0F, 1200.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK), color));
            CONTAINERS.put(color, block);
            CONTAINER_ENTITIES.put(color, ModBlockEntities.BLOCK_ENTITIES.register(name,
                    () -> BlockEntityType.Builder.<ShippingContainerBlock.Entity>of((pos, state) -> new ShippingContainerBlock.Entity(CONTAINER_ENTITIES.get(color).get(), pos, state), block.get()).build(null)));
            CONTAINER_ITEMS.add(ModItems.ITEMS.register(name, () -> new ShippingContainerItem(block.get(), new Item.Properties())));
        }
    }

    public static void registerEvents(IEventBus modBus) {
        modBus.addListener(CreateDecoBlocks::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (var type : CONTAINER_ENTITIES.values()) {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type.get(), (be, side) -> be.getItemHandler());
        }
    }

    public static void registerMountedStorage() {
        MountedItemStorageType<?> vault = MountedItemStorageType.REGISTRY.get(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("create", "item_vault")));
        if (vault == null) return;
        for (var block : CONTAINERS.values()) {
            MountedItemStorageType.REGISTRY.register(block.get(), vault);
        }
    }

    public static void addToTabs(BuildCreativeModeTabContentsEvent event, CreativeModeTab.TabVisibility vis) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            CATWALK_ITEMS.forEach(item -> event.accept(item.get(), vis));
        }
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            CONTAINER_ITEMS.forEach(item -> event.accept(item.get(), vis));
        }
    }

    private static BlockBehaviour.Properties catwalkProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .sound(SoundType.NETHERITE_BLOCK);
    }
}
