package com.evandev.spicedcider.client.screens;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AssetSelectionScreen extends Screen {
    private final Set<ResourceLocation> availableAssets;
    private final Map<Checkbox, ResourceLocation> checkboxes = new HashMap<>();

    public AssetSelectionScreen(Set<ResourceLocation> availableAssets) {
        super(Component.literal("Select Assets to Extract"));
        this.availableAssets = availableAssets;
    }

    private static @NotNull Path getTargetFile(ResourceLocation fileResource) {
        String namespace = fileResource.getNamespace();
        String subPath = fileResource.getPath();

        Path basePath = FMLPaths.GAMEDIR.get()
                .resolve("resourcepacks")
                .resolve("spicedcider_resources")
                .resolve("assets")
                .resolve(namespace);

        return basePath.resolve(subPath);
    }

    @Override
    protected void init() {
        checkboxes.clear();
        int y = 40;
        int x = this.width / 2 - 150;

        for (ResourceLocation asset : availableAssets) {
            Checkbox checkbox = Checkbox.builder(Component.literal(asset.toString()), this.font)
                    .pos(x, y)
                    .selected(true)
                    .build();

            this.addRenderableWidget(checkbox);
            checkboxes.put(checkbox, asset);
            y += 24;

            if (y > this.height - 60) {
                y = 40;
                x += 160;
            }
        }

        this.addRenderableWidget(Button.builder(Component.literal("Extract"), btn -> extractSelected())
                .bounds(this.width / 2 - 105, this.height - 30, 100, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> this.onClose())
                .bounds(this.width / 2 + 5, this.height - 30, 100, 20)
                .build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }

    private void extractSelected() {
        Set<Path> directoriesToOpen = new HashSet<>();

        for (Map.Entry<Checkbox, ResourceLocation> entry : checkboxes.entrySet()) {
            if (entry.getKey().selected()) {
                Path targetDir = copyAsset(entry.getValue());
                if (targetDir != null) {
                    directoriesToOpen.add(targetDir);
                }
            }
        }

        for (Path dir : directoriesToOpen) {
            Util.getPlatform().openUri(dir.toUri());
        }

        this.onClose();
    }

    private Path copyAsset(ResourceLocation resource) {
        Path targetFile = getTargetFile(resource);
        Path targetDir = targetFile.getParent();

        try {
            Files.createDirectories(targetDir);
            var resourceManager = Minecraft.getInstance().getResourceManager();
            var resourceOpt = resourceManager.getResource(resource);

            if (resourceOpt.isPresent()) {
                if (!Files.exists(targetFile)) {
                    try (InputStream in = resourceOpt.get().open()) {
                        Files.copy(in, targetFile);
                        SpicedCider.LOGGER.info("Successfully extracted asset to {}", targetFile);
                        return targetDir;
                    }
                } else {
                    return targetDir; // Already exists, but we still might want to open the folder
                }
            } else {
                SpicedCider.LOGGER.warn("Could not find the resource for {}", resource);
            }
        } catch (Exception e) {
            SpicedCider.LOGGER.error("Failed to extract asset: {}", targetFile, e);
        }
        return null;
    }
}