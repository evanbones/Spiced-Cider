package com.evandev.spicedcider.client.tooltrims;

import com.evandev.spicedcider.SpicedCider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ToolTrimSpriteSource implements SpriteSource {
    public static final ToolTrimSpriteSource INSTANCE = new ToolTrimSpriteSource();
    public static final MapCodec<ToolTrimSpriteSource> CODEC = MapCodec.unit(INSTANCE);
    public static SpriteSourceType TYPE;

    private static final String PATTERN_DIR = "textures/trims/models/armor";
    private static final int[][] MOTIF_REGIONS = {{20, 20, 28, 32}, {8, 8, 16, 16}, {44, 20, 48, 32}, {4, 20, 8, 32}};
    private static final int[] HELMET_REGION = {8, 8, 16, 16};
    private static final int[] CHESTPLATE_REGION = {20, 20, 28, 32};
    private static final int[] LEG_REGION = {4, 20, 8, 32};
    private static final int[] BOOT_REGION = {4, 26, 8, 32};

    @Override
    public void run(ResourceManager manager, Output output) {
        Map<ResourceLocation, NativeImage> patterns = new LinkedHashMap<>();
        manager.listResources(PATTERN_DIR, location -> location.getPath().endsWith(".png")).forEach((location, resource) -> {
            String name = location.getPath().substring(PATTERN_DIR.length() + 1, location.getPath().length() - 4);
            if (name.contains("/") || name.startsWith("card_")) return;
            NativeImage image = read(resource);
            if (image != null) patterns.put(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), name), image);
        });

        int count = 0;
        Map<ResourceLocation, NativeImage> tools = new HashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            boolean armor = item instanceof ArmorItem armorItem && armorItem.getType() != ArmorItem.Type.BODY;
            if (!armor && !ToolTrims.candidate(item)) continue;
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            ResourceLocation texture = layer0(manager, itemId);
            if (texture == null) continue;
            NativeImage tool = tools.computeIfAbsent(texture, t -> manager.getResource(t.withPath(p -> "textures/" + p + ".png")).map(ToolTrimSpriteSource::read).orElse(null));
            if (tool == null) continue;
            Mask mask = Mask.of(tool, !armor);
            if (mask == null) continue;
            if (armor) {
                ArmorItem.Type type = ((ArmorItem) item).getType();
                for (Map.Entry<ResourceLocation, NativeImage> pattern : patterns.entrySet()) {
                    String path = pattern.getKey().getPath();
                    boolean leggingsTexture = path.endsWith("_leggings");
                    if (leggingsTexture != (type == ArmorItem.Type.LEGGINGS)) continue;
                    NativeImage overlay = switch (type) {
                        case HELMET -> project(tool, mask, pattern.getValue(), HELMET_REGION, false);
                        case CHESTPLATE -> project(tool, mask, pattern.getValue(), CHESTPLATE_REGION, false);
                        case LEGGINGS -> project(tool, mask, pattern.getValue(), LEG_REGION, true);
                        default -> project(tool, mask, pattern.getValue(), BOOT_REGION, true);
                    };
                    if (overlay == null) continue;
                    ResourceLocation patternId = leggingsTexture ? pattern.getKey().withPath(path.substring(0, path.length() - "_leggings".length())) : pattern.getKey();
                    ResourceLocation id = ToolTrims.armorOverlay(itemId, patternId);
                    output.add(id, loader -> new SpriteContents(id, new FrameSize(overlay.getWidth(), overlay.getHeight()), overlay, ResourceMetadata.EMPTY));
                    count++;
                }
                continue;
            }
            for (Map.Entry<ResourceLocation, NativeImage> pattern : patterns.entrySet()) {
                if (pattern.getKey().getPath().endsWith("_leggings")) continue;
                NativeImage overlay = overlay(tool, mask, pattern.getValue());
                if (overlay == null) continue;
                ResourceLocation id = ToolTrims.overlay(itemId, pattern.getKey());
                output.add(id, loader -> new SpriteContents(id, new FrameSize(overlay.getWidth(), overlay.getHeight()), overlay, ResourceMetadata.EMPTY));
                count++;
            }
        }
        tools.values().forEach(image -> {
            if (image != null) image.close();
        });
        patterns.values().forEach(NativeImage::close);
        SpicedCider.LOGGER.debug("Generated {} tool and armor trim overlays", count);
    }

    @Override
    public SpriteSourceType type() {
        return TYPE;
    }

    private static @Nullable NativeImage overlay(NativeImage tool, Mask mask, NativeImage pattern) {
        int scale = Math.max(1, pattern.getWidth() / 64);
        int[] region = null;
        int best = 0;
        for (int[] candidate : MOTIF_REGIONS) {
            int opaque = 0;
            for (int x = candidate[0] * scale; x < candidate[2] * scale; x++) {
                for (int y = candidate[1] * scale; y < candidate[3] * scale; y++) {
                    if (alpha(pattern.getPixelRGBA(x, y)) > 0) opaque++;
                }
            }
            if (opaque > best) {
                best = opaque;
                region = candidate;
            }
        }
        if (region == null) return null;
        return project(tool, mask, pattern, region, false);
    }

    private static @Nullable NativeImage project(NativeImage tool, Mask mask, NativeImage pattern, int[] region, boolean mirrored) {
        int scale = Math.max(1, pattern.getWidth() / 64);
        int x0 = region[0] * scale, y0 = region[1] * scale;
        int mw = (region[2] - region[0]) * scale, mh = (region[3] - region[1]) * scale;
        int half = Math.max(1, (mask.width() + 1) / 2);
        NativeImage out = new NativeImage(tool.getWidth(), tool.getHeight(), true);
        boolean any = false;
        for (int x = 0; x < tool.getWidth(); x++) {
            for (int y = 0; y < tool.getHeight(); y++) {
                if (!mask.inner(x, y)) continue;
                int mx;
                if (mirrored) {
                    int local = x - mask.minX < half ? x - mask.minX : mask.maxX - x;
                    mx = x0 + Math.min(mw - 1, local * mw / half);
                } else {
                    mx = x0 + (x - mask.minX) * mw / mask.width();
                }
                int my = y0 + (y - mask.minY) * mh / mask.height();
                int color = pattern.getPixelRGBA(mx, my);
                if (alpha(color) == 0) continue;
                int grey = 150 + luminance(color) * 105 / 255;
                out.setPixelRGBA(x, y, 0xFF000000 | grey << 16 | grey << 8 | grey);
                any = true;
            }
        }
        if (!any) {
            out.close();
            return null;
        }
        return out;
    }

    private static @Nullable ResourceLocation layer0(ResourceManager manager, ResourceLocation itemId) {
        Map<String, String> textures = new HashMap<>();
        ResourceLocation model = itemId.withPrefix("item/");
        for (int depth = 0; depth < 8 && model != null; depth++) {
            Optional<Resource> resource = manager.getResource(model.withPath(p -> "models/" + p + ".json"));
            if (resource.isEmpty()) return null;
            JsonObject json;
            try (Reader reader = resource.get().openAsReader()) {
                json = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (IOException | RuntimeException e) {
                return null;
            }
            if (json.has("elements") || json.has("loader")) return null;
            if (json.has("textures")) {
                for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("textures").entrySet()) {
                    textures.putIfAbsent(entry.getKey(), entry.getValue().getAsString());
                }
            }
            String parent = json.has("parent") ? json.get("parent").getAsString() : null;
            if (parent == null || parent.equals("builtin/entity") || parent.endsWith("builtin/entity")) return null;
            ResourceLocation parentId = ResourceLocation.parse(parent);
            if (parentId.getNamespace().equals("minecraft") && (parentId.getPath().equals("item/generated") || parentId.getPath().startsWith("item/handheld"))) {
                model = null;
            } else {
                model = parentId;
            }
        }
        String layer = textures.get("layer0");
        Set<String> seen = new HashSet<>();
        while (layer != null && layer.startsWith("#") && seen.add(layer)) {
            layer = textures.get(layer.substring(1));
        }
        return layer == null || layer.startsWith("#") ? null : ResourceLocation.parse(layer);
    }

    private static @Nullable NativeImage read(Resource resource) {
        try (InputStream stream = resource.open()) {
            return NativeImage.read(stream);
        } catch (IOException e) {
            return null;
        }
    }

    private static int alpha(int abgr) {
        return abgr >>> 24;
    }

    private static int luminance(int abgr) {
        int r = abgr & 0xFF, g = abgr >> 8 & 0xFF, b = abgr >> 16 & 0xFF;
        return (r * 299 + g * 587 + b * 114) / 1000;
    }

    private record Mask(boolean[][] inner, int minX, int minY, int maxX, int maxY) {

        static @Nullable Mask of(NativeImage tool, boolean excludeHandle) {
            int w = tool.getWidth(), h = tool.getHeight();
            Set<Integer> handle = new HashSet<>();
            for (int x = 0; excludeHandle && x < w * 2 / 5; x++) {
                for (int y = h * 3 / 5; y < h; y++) {
                    int color = tool.getPixelRGBA(x, y);
                    if (alpha(color) > 0) handle.add(color);
                }
            }
            boolean[][] inner = new boolean[w][h];
            int minX = w, minY = h, maxX = -1, maxY = -1;
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int color = tool.getPixelRGBA(x, y);
                    if (alpha(color) == 0 || handle.contains(color)) continue;
                    if (!opaque(tool, x + 1, y) || !opaque(tool, x - 1, y) || !opaque(tool, x, y + 1) || !opaque(tool, x, y - 1)) continue;
                    inner[x][y] = true;
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
            return maxX < 0 ? null : new Mask(inner, minX, minY, maxX, maxY);
        }

        private static boolean opaque(NativeImage image, int x, int y) {
            return x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight() && alpha(image.getPixelRGBA(x, y)) > 0;
        }

        boolean inner(int x, int y) {
            return this.inner[x][y];
        }

        int width() {
            return this.maxX - this.minX + 1;
        }

        int height() {
            return this.maxY - this.minY + 1;
        }
    }
}
