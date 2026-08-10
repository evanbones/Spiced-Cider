package com.evandev.spicedcider.client.resource;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class BrokenToolModelSwap {

    private static final String MODEL_ROOT = "assets/" + SpicedCider.MOD_ID + "/models/item/broken/";
    private static final Map<ResourceLocation, ResourceLocation> BROKEN_MODELS = discoverBrokenModels();

    private static Map<ResourceLocation, ResourceLocation> discoverBrokenModels() {
        Map<ResourceLocation, ResourceLocation> map = new LinkedHashMap<>();

        try {
            Enumeration<URL> roots = BrokenToolModelSwap.class.getClassLoader().getResources(MODEL_ROOT);
            while (roots.hasMoreElements()) {
                discoverFrom(roots.nextElement(), map);
            }
        } catch (IOException e) {
            SpicedCider.LOGGER.error("Failed to discover broken tool models", e);
        }

        return Map.copyOf(map);
    }

    private static void discoverFrom(URL rootUrl, Map<ResourceLocation, ResourceLocation> out) {
        FileSystem createdFs = null;

        try {
            URI uri = rootUrl.toURI();
            Path dir;

            if ("jar".equals(uri.getScheme())) {
                FileSystem fs;
                try {
                    fs = FileSystems.newFileSystem(uri, Map.of());
                    createdFs = fs;
                } catch (FileSystemAlreadyExistsException e) {
                    fs = FileSystems.getFileSystem(uri);
                }
                dir = fs.provider().getPath(uri);
            } else {
                dir = Path.of(uri);
            }

            try (Stream<Path> files = Files.walk(dir)) {
                files.filter(p -> p.getFileName().toString().endsWith(".json")).forEach(p -> {
                    Path relative = dir.relativize(p);
                    if (relative.getNameCount() != 2) return; // should be <namespace>/<path>.json

                    String namespace = relative.getName(0).toString();
                    String fileName = relative.getName(1).toString();
                    String path = fileName.substring(0, fileName.length() - ".json".length());

                    ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(namespace, path);
                    ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "item/broken/" + namespace + "/" + path);
                    out.put(itemId, modelId);
                });
            }
        } catch (Exception e) {
            SpicedCider.LOGGER.error("Failed to read broken tool models from {}", rootUrl, e);
        } finally {
            if (createdFs != null) {
                try {
                    createdFs.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerBrokenModels(ModelEvent.RegisterAdditional event) {
        for (ResourceLocation brokenModelId : BROKEN_MODELS.values()) {
            event.register(ModelResourceLocation.standalone(brokenModelId));
        }
    }

    @SubscribeEvent
    public static void swapBrokenModels(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();

        BROKEN_MODELS.forEach((itemId, brokenModelId) -> {
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item == Items.AIR) return;

            ModelResourceLocation itemModelLoc = ModelResourceLocation.inventory(itemId);
            BakedModel original = models.get(itemModelLoc);
            BakedModel broken = models.get(ModelResourceLocation.standalone(brokenModelId));
            if (original == null || broken == null) return;

            models.put(itemModelLoc, new BrokenSwapBakedModel(original, broken));
        });
    }
}
