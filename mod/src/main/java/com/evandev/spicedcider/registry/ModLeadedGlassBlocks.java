package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.content.block.glass.EightwayConnectedPaneBlock;
import com.evandev.spicedcider.content.block.glass.LeadedGlassPaneBlock;
import com.evandev.spicedcider.content.block.glass.VerticalConnectedPaneBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModLeadedGlassBlocks {
    public static final List<DeferredBlock<Block>> BLOCKS = new ArrayList<>();
    public static final List<DeferredItem<BlockItem>> ITEMS = new ArrayList<>();

    static {
        vertical("leaded_glass_pane");
        for (String color : List.of("white", "light_gray", "gray", "black", "brown", "red", "orange", "yellow", "lime",
                "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink")) {
            eightway(color + "_leaded_glass_pane");
        }
        for (String pattern : List.of("ring", "midland", "gufy", "avalon", "oakfield", "crystal", "coral", "sunrise", "sacred", "imperial")) {
            vertical(pattern + "_leaded_glass_pane");
        }
        for (String motif : List.of("heart", "pickaxe", "rose", "diamond", "apple")) {
            register(motif + "_leaded_glass_pane", LeadedGlassPaneBlock::new);
        }
    }

    public static void register() {
    }

    private static void vertical(String name) {
        register(name, VerticalConnectedPaneBlock::new);
    }

    private static void eightway(String name) {
        register(name, EightwayConnectedPaneBlock::new);
    }

    private static void register(String name, Function<BlockBehaviour.Properties, ? extends Block> factory) {
        DeferredBlock<Block> block = ModBlocks.BLOCKS.register(name, () -> factory.apply(properties()));
        BLOCKS.add(block);
        ITEMS.add(ModItems.ITEMS.registerSimpleBlockItem(block));
    }

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .sound(SoundType.GLASS)
                .strength(2.0F, 0.3F)
                .noOcclusion();
    }
}
