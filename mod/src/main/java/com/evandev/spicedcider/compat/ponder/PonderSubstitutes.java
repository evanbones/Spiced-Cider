package com.evandev.spicedcider.compat.ponder;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class PonderSubstitutes {

    private static final Map<String, String> FALLBACKS = fallbacks();

    private static Map<Block, Block> blocks;
    private static Map<Item, Item> items;
    private static Map<String, Item> descriptions;

    private static Map<String, String> fallbacks() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("create:creative_motor", "create:shaft");
        map.put("create:analog_lever", "minecraft:lever");
        map.put("create:mechanical_drill", "create:mechanical_saw");
        map.put("create:mechanical_harvester", "create:deployer");
        map.put("create:mechanical_plough", "create:deployer");
        map.put("create:creative_fluid_tank", "create:fluid_tank");
        map.put("create:encased_fan", "minecraft:air");
        map.put("create:train_door", "minecraft:iron_door");
        map.put("create:copycat_step", "minecraft:air");
        map.put("create:copycat_panel", "minecraft:air");
        map.put("create:schematicannon", "minecraft:air");
        map.put("create:propeller", "create:cogwheel");
        map.put("create:brass_hand", "minecraft:air");
        map.put("create:andesite_alloy", "alloyed:bronze_ingot");
        map.put("create:andesite_alloy_block", "alloyed:bronze_block");
        map.put("minecraft:iron_pickaxe", "alloyed:steel_pickaxe");
        map.put("minecraft:emerald", "minecraft:diamond");
        for (DyeColor color : DyeColor.values()) {
            map.put("create:" + color.getSerializedName() + "_seat", "quark:" + color.getSerializedName() + "_stool");
            map.put("create:" + color.getSerializedName() + "_postbox", "minecraft:air");
        }
        return map;
    }

    public static boolean inPonder() {
        return Minecraft.getInstance().screen instanceof PonderUI;
    }

    public static BlockState block(BlockState state) {
        Block target = blocks().get(state.getBlock());
        if (target == null) return state;
        return copyProperties(state, target.defaultBlockState());
    }

    public static ItemStack item(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        Item target = items().get(stack.getItem());
        if (target == null) return stack;
        return target == Items.AIR ? ItemStack.EMPTY : stack.transmuteCopy(target, stack.getCount());
    }

    public static ItemStack renderedItem(ItemStack stack) {
        return inPonder() ? item(stack) : stack;
    }

    public static Component text(Component text) {
        if (!(text.getContents() instanceof TranslatableContents translatable)) return text;
        if (descriptions == null) {
            descriptions = new HashMap<>();
            items().forEach((from, to) -> descriptions.put(from.getDescriptionId(), to));
        }
        Item target = descriptions.get(translatable.getKey());
        if (target == null) return text;
        return target == Items.AIR ? Component.empty() : target.getDescription();
    }

    private static Map<Block, Block> blocks() {
        if (blocks == null) build();
        return blocks;
    }

    private static Map<Item, Item> items() {
        if (items == null) build();
        return items;
    }

    private static void build() {
        Map<String, String> ids = new LinkedHashMap<>(FALLBACKS);
        ids.putAll(removerReplacements());
        Map<Block, Block> blockMap = new HashMap<>();
        Map<Item, Item> itemMap = new HashMap<>();
        ids.forEach((from, to) -> {
            ResourceLocation fromId = ResourceLocation.tryParse(from);
            ResourceLocation toId = ResourceLocation.tryParse(to);
            if (fromId == null || toId == null) return;
            Optional<Item> fromItem = BuiltInRegistries.ITEM.getOptional(fromId);
            if (fromItem.isEmpty() || !ReliableRemoverCompat.isRemoved(fromItem.get())) return;
            Optional<Item> toItem = BuiltInRegistries.ITEM.getOptional(toId);
            if (toItem.isPresent()) {
                itemMap.put(fromItem.get(), toItem.get());
            }
            Optional<Block> fromBlock = BuiltInRegistries.BLOCK.getOptional(fromId);
            Optional<Block> toBlock = BuiltInRegistries.BLOCK.getOptional(toId);
            if (fromBlock.isPresent() && toBlock.isPresent()) {
                blockMap.put(fromBlock.get(), toBlock.get());
            }
        });
        blocks = blockMap;
        items = itemMap;
    }

    private static Map<String, String> removerReplacements() {
        Map<String, String> map = new HashMap<>();
        Path dir = FMLPaths.CONFIGDIR.get().resolve("reliable_remover").resolve("mods");
        if (!Files.isDirectory(dir)) return map;
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(file -> file.toString().endsWith(".json")).forEach(file -> readReplacements(file, map));
        } catch (IOException e) {
            SpicedCider.LOGGER.warn("Failed to list Reliable Remover rules for Ponder substitutions", e);
        }
        return map;
    }

    private static void readReplacements(Path file, Map<String, String> map) {
        try {
            JsonElement root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
            if (!root.isJsonArray()) return;
            for (JsonElement element : root.getAsJsonArray()) {
                if (!element.isJsonObject()) continue;
                JsonObject rule = element.getAsJsonObject();
                if (!rule.has("replace_with") || !rule.has("items")) continue;
                String target = rule.get("replace_with").getAsString();
                for (JsonElement item : rule.getAsJsonArray("items")) {
                    String id = item.getAsString();
                    if (!id.startsWith("#")) map.put(id, target);
                }
            }
        } catch (Exception e) {
            SpicedCider.LOGGER.warn("Failed to read Reliable Remover rules from {} for Ponder substitutions", file, e);
        }
    }

    private static BlockState copyProperties(BlockState from, BlockState to) {
        for (Property<?> property : from.getProperties()) {
            to = copyProperty(from, to, property);
        }
        if (from.hasProperty(BlockStateProperties.FACING) && to.hasProperty(BlockStateProperties.AXIS)) {
            Direction facing = from.getValue(BlockStateProperties.FACING);
            to = to.setValue(BlockStateProperties.AXIS, facing.getAxis());
        }
        return to;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        Property<?> target = to.getBlock().getStateDefinition().getProperty(property.getName());
        if (target == null) return to;
        return withValue(to, target, property.getName(from.getValue(property)));
    }

    private static <T extends Comparable<T>> BlockState withValue(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(parsed -> state.setValue(property, parsed)).orElse(state);
    }
}
