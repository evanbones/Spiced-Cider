package com.evandev.spicedcider.client.tooltrims;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.smithing.TrimStyling;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ToolTrimModels {

    private static final Map<String, Integer> TINTS = new ConcurrentHashMap<>();
    private static final Map<String, List<BakedQuad>> CACHE = new ConcurrentHashMap<>();
    private static final String MODEFITE_COMPOSITE = "timmychips.modefiteitemdefinitions.bakedmodels.CompositeItemModel";
    private static final Map<String, Boolean> COLLIDES = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void registerSpriteSource(RegisterSpriteSourceTypesEvent event) {
        ToolTrimSpriteSource.TYPE = event.register(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "tool_trims"), ToolTrimSpriteSource.CODEC);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void wrapToolModels(ModelEvent.ModifyBakingResult event) {
        TINTS.clear();
        CACHE.clear();
        COLLIDES.clear();
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        for (Item item : BuiltInRegistries.ITEM) {
            if (!ToolTrims.candidate(item) && !(item instanceof ArmorItem)) continue;
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            ModelResourceLocation location = ModelResourceLocation.inventory(id);
            BakedModel model = models.get(location);
            if (model == null || model.isCustomRenderer()) continue;
            models.put(location, new Trimmable(model));
        }
    }

    private static boolean collides(Holder<TrimMaterial> material) {
        String key = material.unwrapKey().map(k -> k.location().toString()).orElse(material.value().assetName());
        return COLLIDES.computeIfAbsent(key, k -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return false;
            return level.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL).stream()
                    .anyMatch(other -> other.itemModelIndex() == material.value().itemModelIndex() && !other.assetName().equals(material.value().assetName()));
        });
    }

    private static int tint(Holder<TrimMaterial> material) {
        return tint(material, material.value().assetName());
    }

    private static int tint(Holder<TrimMaterial> material, String asset) {
        String key = material.unwrapKey().map(k -> k.location().toString()).orElse("") + "|" + asset;
        return TINTS.computeIfAbsent(key, k -> paletteTint(material, asset));
    }

    private static int paletteTint(Holder<TrimMaterial> material, String asset) {
        List<ResourceLocation> candidates = new ArrayList<>();
        if (asset.equals(material.value().assetName())) {
            material.unwrapKey().ifPresent(key -> candidates.add(key.location().withPath(p -> "textures/trims/color_palettes/" + p + ".png")));
        } else {
            material.unwrapKey().ifPresent(key -> candidates.add(key.location().withPath("textures/trims/color_palettes/" + asset + ".png")));
        }
        candidates.add(ResourceLocation.withDefaultNamespace("textures/trims/color_palettes/" + asset + ".png"));
        int split = asset.indexOf('_');
        if (split > 0) {
            candidates.add(ResourceLocation.fromNamespaceAndPath(asset.substring(0, split), "textures/trims/color_palettes/" + asset.substring(split + 1) + ".png"));
        }
        for (ResourceLocation candidate : candidates) {
            var resource = Minecraft.getInstance().getResourceManager().getResource(candidate);
            if (resource.isEmpty()) continue;
            try (InputStream stream = resource.get().open(); NativeImage palette = NativeImage.read(stream)) {
                int abgr = palette.getPixelRGBA(Math.min(1, palette.getWidth() - 1), 0);
                return 0xFF000000 | (abgr & 0xFF) << 16 | (abgr >> 8 & 0xFF) << 8 | (abgr >> 16 & 0xFF);
            } catch (IOException ignored) {
            }
        }
        TextColor color = material.value().description().getStyle().getColor();
        return 0xFF000000 | (color == null ? 0xFFFFFF : color.getValue());
    }

    public static BakedModel atCallSite(BakedModel resolved, ItemRenderer renderer, ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed) {
        if (!stack.has(DataComponents.TRIM)) return resolved;
        return apply(resolved, stack, () -> {
            ItemStack untrimmed = stack.copy();
            untrimmed.remove(DataComponents.TRIM);
            return renderer.getModel(untrimmed, level, entity, seed);
        });
    }

    public static BakedModel apply(BakedModel resolved, ItemStack stack, Supplier<BakedModel> plain) {
        if (resolved instanceof Overlaid) return resolved;
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) return resolved;
        if (stack.getItem() instanceof ArmorItem armor && stack.is(ItemTags.TRIMMABLE_ARMOR)) {
            BakedModel untrimmed = plain.get();
            BakedModel patterned = armorPatterned(untrimmed, armor, trim);
            if (patterned != null) return patterned;
            if (resolved != untrimmed && !isModefite(resolved) && !collides(trim.material())) return resolved;
            return armorTrimmed(untrimmed, armor, trim);
        }
        if (!stack.is(TrimStyling.TRIMMABLE_TOOLS)) return resolved;
        return toolTrimmed(resolved, BuiltInRegistries.ITEM.getKey(stack.getItem()), trim);
    }

    private static BakedModel armorTrimmed(BakedModel base, ArmorItem armor, ArmorTrim trim) {
        String slot = switch (armor.getType()) {
            case HELMET -> "helmet";
            case CHESTPLATE -> "chestplate";
            case LEGGINGS -> "leggings";
            case BOOTS -> "boots";
            default -> null;
        };
        if (slot == null) return base;
        TrimMaterial material = trim.material().value();
        String suffix = material.overrideArmorMaterials().getOrDefault(armor.getMaterial(), material.assetName());
        ResourceLocation spriteId = ResourceLocation.withDefaultNamespace("trims/items/" + slot + "_trim_" + suffix);
        List<BakedQuad> quads = CACHE.computeIfAbsent(spriteId.toString(), k -> {
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(spriteId);
            if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) return List.of();
            return UnbakedGeometryHelper.bakeElements(UnbakedGeometryHelper.createUnbakedItemElements(-1, sprite), m -> sprite, BlockModelRotation.X0_Y0);
        });
        return quads.isEmpty() ? base : new Overlaid(base, quads);
    }

    private static @Nullable BakedModel armorPatterned(BakedModel base, ArmorItem armor, ArmorTrim trim) {
        ResourceLocation item = BuiltInRegistries.ITEM.getKey(armor);
        ResourceLocation pattern = trim.pattern().value().assetId();
        TrimMaterial material = trim.material().value();
        String suffix = material.overrideArmorMaterials().getOrDefault(armor.getMaterial(), material.assetName());
        String key = item + "|" + pattern + "|" + suffix + "|" + trim.material().unwrapKey().map(k -> k.location().toString()).orElse("");
        List<BakedQuad> quads = CACHE.computeIfAbsent(key, k -> {
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(ToolTrims.armorOverlay(item, pattern));
            if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) return List.of();
            List<BakedQuad> overlay = UnbakedGeometryHelper.bakeElements(
                    UnbakedGeometryHelper.createUnbakedItemElements(-1, sprite), m -> sprite, BlockModelRotation.X0_Y0);
            return QuadTransformers.applyingColor(tint(trim.material(), suffix)).process(overlay);
        });
        return quads.isEmpty() ? null : new Overlaid(base, quads);
    }

    private static BakedModel toolTrimmed(BakedModel base, ResourceLocation item, ArmorTrim trim) {
        ResourceLocation pattern = trim.pattern().value().assetId();
        String key = item + "|" + pattern + "|" + trim.material().unwrapKey().map(k -> k.location().toString()).orElse("");
        List<BakedQuad> quads = CACHE.computeIfAbsent(key, k -> {
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(ToolTrims.overlay(item, pattern));
            if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) return List.of();
            List<BakedQuad> overlay = UnbakedGeometryHelper.bakeElements(
                    UnbakedGeometryHelper.createUnbakedItemElements(-1, sprite), material -> sprite, BlockModelRotation.X0_Y0);
            return QuadTransformers.applyingColor(tint(trim.material())).process(overlay);
        });
        return quads.isEmpty() ? base : new Overlaid(base, quads);
    }

    private static boolean isModefite(BakedModel model) {
        return model.getClass().getName().equals(MODEFITE_COMPOSITE);
    }

    private static class Trimmable extends BakedModelWrapper<BakedModel> {
        private final ItemOverrides overrides;

        Trimmable(BakedModel original) {
            super(original);
            ItemOverrides base = original.getOverrides();
            this.overrides = new ItemOverrides() {
                @Override
                public @Nullable BakedModel resolve(@NotNull BakedModel model, @NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
                    BakedModel resolved = base.resolve(Trimmable.this.originalModel, stack, level, entity, seed);
                    if (resolved == null) resolved = Trimmable.this.originalModel;
                    return apply(resolved, stack, () -> {
                        ItemStack untrimmed = stack.copy();
                        untrimmed.remove(DataComponents.TRIM);
                        BakedModel plain = base.resolve(Trimmable.this.originalModel, untrimmed, level, entity, seed);
                        return plain == null ? Trimmable.this.originalModel : plain;
                    });
                }
            };
        }

        @Override
        public @NotNull ItemOverrides getOverrides() {
            return this.overrides;
        }
    }

    private static class Overlaid extends BakedModelWrapper<BakedModel> {
        private final List<BakedQuad> overlay;

        Overlaid(BakedModel base, List<BakedQuad> overlay) {
            super(base);
            this.overlay = overlay;
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
            List<BakedQuad> quads = super.getQuads(state, side, rand);
            if (side != null) return quads;
            List<BakedQuad> all = new ArrayList<>(quads);
            all.addAll(this.overlay);
            return all;
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable net.minecraft.client.renderer.RenderType renderType) {
            List<BakedQuad> quads = super.getQuads(state, side, rand, data, renderType);
            if (side != null) return quads;
            List<BakedQuad> all = new ArrayList<>(quads);
            all.addAll(this.overlay);
            return all;
        }

        @Override
        public @NotNull BakedModel applyTransform(@NotNull ItemDisplayContext type, @NotNull PoseStack poseStack, boolean applyLeftHandTransform) {
            BakedModel transformed = this.originalModel.applyTransform(type, poseStack, applyLeftHandTransform);
            return transformed == this.originalModel ? this : transformed;
        }

        @Override
        public @NotNull List<BakedModel> getRenderPasses(@NotNull ItemStack stack, boolean fabulous) {
            return List.of(this);
        }
    }
}
