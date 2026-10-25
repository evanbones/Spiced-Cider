package com.evandev.spicedcider.registry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModBuildingBlocks {
    public enum Kind { CUBE, PILLAR, STAIRS, SLAB, WALL }

    public enum Craft { NONE, SHAPE, MOSSY, POLISH, PILLAR, TILES }

    public record Def(String id, Kind kind, @Nullable String mod, String properties, @Nullable String parent,
                      String side, String top, String bottom, Craft craft, @Nullable String craftFrom,
                      List<String> cut, List<String> requires, String tool) {
        public String path() {
            return ResourceLocation.parse(id).getPath();
        }

        public ResourceLocation location() {
            return ResourceLocation.parse(id);
        }

        public boolean available() {
            return mod == null || ModList.get().isLoaded(mod);
        }
    }

    public static final List<Def> DEFS = load();
    public static final Map<String, DeferredBlock<Block>> BLOCKS = new LinkedHashMap<>();
    public static final Map<String, DeferredItem<BlockItem>> ITEMS = new LinkedHashMap<>();

    private static List<Def> load() {
        try (InputStream in = ModBuildingBlocks.class.getResourceAsStream("/spicedcider/building_blocks.json")) {
            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), new TypeToken<List<Def>>() {
            }.getType());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read building block definitions", e);
        }
    }

    public static void register() {
        for (Def def : DEFS) {
            if (!def.available()) continue;
            DeferredBlock<Block> block = ModBlocks.BLOCKS.register(def.path(), () -> create(def));
            BLOCKS.put(def.id(), block);
            ITEMS.put(def.id(), ModItems.ITEMS.registerSimpleBlockItem(block));
        }
    }

    private static Block lookup(String id) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id));
        if (block == Blocks.AIR) {
            throw new IllegalStateException("Building block source " + id + " is not registered yet");
        }
        return block;
    }

    private static Block create(Def def) {
        Block source = lookup(def.properties());
        BlockState sourceState = source.defaultBlockState();
        int light = sourceState.getLightEmission();
        MapColor mapColor = sourceState.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.ofFullCopy(source)
                .lightLevel(state -> light)
                .mapColor(mapColor);
        return switch (def.kind()) {
            case CUBE -> new Block(properties);
            case PILLAR -> new RotatedPillarBlock(properties);
            case STAIRS -> new StairBlock(lookup(def.parent()).defaultBlockState(), properties);
            case SLAB -> new SlabBlock(properties);
            case WALL -> new WallBlock(properties.forceSolidOn());
        };
    }
}
