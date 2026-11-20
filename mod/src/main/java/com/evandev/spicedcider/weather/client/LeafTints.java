package com.evandev.spicedcider.weather.client;

import com.evandev.spicedcider.SpicedCider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.Block;

import java.io.Reader;
import java.util.Map;

public final class LeafTints {
    private static final ResourceLocation FILE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "leaf_tints.json");

    public static void apply() {
        Minecraft minecraft = Minecraft.getInstance();
        for (Resource resource : minecraft.getResourceManager().getResourceStack(FILE)) {
            try (Reader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                    if (id == null || !BuiltInRegistries.BLOCK.containsKey(id)) continue;
                    Block block = BuiltInRegistries.BLOCK.get(id);
                    int color = 0xFF000000 | Integer.parseInt(entry.getValue().getAsString().replace("#", ""), 16);
                    BlockColor blockColor = (state, level, pos, tintIndex) -> color;
                    ItemColor itemColor = (stack, tintIndex) -> color;
                    minecraft.getBlockColors().register(blockColor, block);
                    if (block.asItem() != net.minecraft.world.item.Items.AIR) {
                        minecraft.getItemColors().register(itemColor, block);
                    }
                }
            } catch (Exception e) {
                SpicedCider.LOGGER.error("Could not read leaf tints from {}", resource.sourcePackId(), e);
            }
        }
    }
}
