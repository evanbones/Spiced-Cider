package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.create.deco.CreateDecoBlocks;
import com.evandev.spicedcider.compat.create.deco.client.CreateDecoClient;
import com.evandev.spicedcider.content.block.deco.DecalBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModDecoBlocks {
    public static final List<String> DECALS = List.of("warning", "creeper", "skull", "flow", "ice", "radioactive",
            "top_left", "up", "top_right", "left", "cross", "right", "down_left", "down", "down_right",
            "fluid", "fire", "electrical", "fire_diamond", "no_entry");
    public static final List<String> CATWALK_METALS = List.of("iron", "copper", "tin", "bronze", "steel", "cast_iron");
    public static final List<DyeColor> CONTAINER_COLORS = Arrays.asList(DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY,
            DyeColor.BLACK, DyeColor.BROWN, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.GREEN,
            DyeColor.CYAN, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK);

    public static final List<DeferredBlock<DecalBlock>> DECAL_BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> DECAL_ITEMS = new ArrayList<>();

    public static void register() {
        for (String type : DECALS) {
            DeferredBlock<DecalBlock> block = ModBlocks.BLOCKS.register(type + "_decal", () -> new DecalBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
                    .instabreak()
                    .pushReaction(PushReaction.DESTROY)
                    .isViewBlocking((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)));
            DECAL_BLOCKS.add(block);
            DECAL_ITEMS.add(ModItems.ITEMS.registerSimpleBlockItem(block));
        }
        if (ModList.get().isLoaded("create")) {
            CreateDecoBlocks.register();
        }
    }

    public static void registerEvents(IEventBus modBus) {
        if (ModList.get().isLoaded("create")) {
            CreateDecoBlocks.registerEvents(modBus);
        }
    }

    public static void commonSetup() {
        if (ModList.get().isLoaded("create")) {
            CreateDecoBlocks.registerMountedStorage();
        }
    }

    public static void registerClient(IEventBus modBus) {
        if (ModList.get().isLoaded("create")) {
            CreateDecoClient.register(modBus);
        }
    }

    public static void addToTabs(BuildCreativeModeTabContentsEvent event, CreativeModeTab.TabVisibility vis) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            DECAL_ITEMS.forEach(item -> event.accept(item.get(), vis));
        }
        if (ModList.get().isLoaded("create")) {
            CreateDecoBlocks.addToTabs(event, vis);
        }
    }

    public static List<ResourceLocation> createBlockIds() {
        List<ResourceLocation> ids = new ArrayList<>();
        for (String metal : CATWALK_METALS) {
            ids.add(id(metal + "_catwalk"));
            ids.add(id(metal + "_catwalk_stairs"));
            ids.add(id(metal + "_catwalk_railing"));
        }
        for (DyeColor color : CONTAINER_COLORS) {
            ids.add(id(color.getName() + "_shipping_container"));
        }
        return ids;
    }

    public static List<ResourceLocation> catwalkIds() {
        return createBlockIds().stream().filter(id -> id.getPath().contains("catwalk")).toList();
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, path);
    }
}
