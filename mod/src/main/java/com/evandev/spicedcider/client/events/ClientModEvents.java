package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.client.MapIconProperty;
import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.models.armor.MischiefArmorModel;
import com.evandev.spicedcider.client.models.projectile.CobwebProjectileModel;
import com.evandev.spicedcider.client.nether.NetherFogBanks;
import com.evandev.spicedcider.client.nether.NetherHeatHaze;
import com.evandev.spicedcider.client.particle.CleaverSweepParticle;
import com.evandev.spicedcider.client.particle.VentSmokeParticle;
import com.evandev.spicedcider.client.resource.BrokenTextureReloadListener;
import com.evandev.spicedcider.client.music.PocketJukeboxClient;
import com.evandev.spicedcider.client.renderer.PocketJukeboxTooltipRenderer;
import com.evandev.spicedcider.client.renderer.balloon.BalloonRenderer;
import com.evandev.spicedcider.client.models.sputter.SputterArrowfulModel;
import com.evandev.spicedcider.client.models.sputter.SputterModel;
import com.evandev.spicedcider.client.models.strawgolem.StrawGolemModel;
import com.evandev.spicedcider.client.renderer.sputter.SputterRenderer;
import com.evandev.spicedcider.client.renderer.strawgolem.StrawGolemRenderer;
import com.evandev.spicedcider.content.block.FruitLogBlock;
import com.evandev.spicedcider.content.item.PocketJukeboxTooltip;
import com.evandev.spicedcider.client.renderer.projectiles.CobwebProjectileRenderer;
import com.evandev.spicedcider.client.renderer.projectiles.GrapplingHookRenderer;
import com.evandev.spicedcider.compat.ponder.PonderCompat;
import com.evandev.spicedcider.compat.tide.TideCompat;
import com.evandev.spicedcider.content.block.TubewormBlock;
import com.evandev.spicedcider.registry.*;
import com.evandev.spicedcider.resource.ResourceBaker;
import com.evandev.spicedcider.weather.client.CloudCoverClient;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.FoliageColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import com.evandev.spicedcider.compat.controlify.CarvingWheelScreenProcessor;
import com.evandev.spicedcider.compat.controlify.ControlifySettings;
import com.evandev.spicedcider.compat.f3backport.LodDebugEntry;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import java.io.IOException;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {
    private static final ResourceLocation PLAYING = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "playing");
    private static final ResourceLocation MAP_ICON = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "map_icon");

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.COBWEB_PROJECTILE.get(), CobwebProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BALLOON.get(), BalloonRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.SPUTTER.get(), SputterRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.STRAW_GOLEM.get(), StrawGolemRenderer::new);
        if (TideCompat.isLoaded()) {
            event.registerEntityRenderer(ModEntityTypes.GRAPPLING_HOOK.get(), GrapplingHookRenderer::new);
        }
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.COBWEB_PROJECTILE, CobwebProjectileModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.BALLOON, BalloonRenderer::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SPUTTER, SputterModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SPUTTER_ARROWFUL, SputterArrowfulModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.STRAW_GOLEM, StrawGolemModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.CLEAVER_SWEEP.get(), CleaverSweepParticle.Factory::new);
        event.registerSpriteSet(ModParticleTypes.WHITE_VENT_SMOKE.get(), sprites -> new VentSmokeParticle.Provider(sprites, 0.92F));
        event.registerSpriteSet(ModParticleTypes.BLACK_VENT_SMOKE.get(), sprites -> new VentSmokeParticle.Provider(sprites, 0.1F));
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions mischiefArmorExtensions = new IClientItemExtensions() {
            @Override
            public net.minecraft.client.model.HumanoidModel<?> getHumanoidArmorModel(
                    net.minecraft.world.entity.LivingEntity livingEntity,
                    net.minecraft.world.item.ItemStack itemStack,
                    net.minecraft.world.entity.EquipmentSlot equipmentSlot,
                    net.minecraft.client.model.HumanoidModel<?> original
            ) {
                return MischiefArmorModel.getModel(equipmentSlot, livingEntity);
            }
        };
        event.registerItem(mischiefArmorExtensions,
                ModItems.MISCHIEF_HELMET.get(),
                ModItems.MISCHIEF_CHESTPLATE.get(),
                ModItems.MISCHIEF_LEGGINGS.get(),
                ModItems.MISCHIEF_BOOTS.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> tintIndex == 0 ? tubewormColor(state.getValue(TubewormBlock.COLOR)) : -1, ModBlocks.TUBEWORM.get());
        event.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(),
                ModBlocks.CACAO_LEAVES.get(), ModBlocks.FLOWERING_CACAO_LEAVES.get(), ModBlocks.PEAR_LEAVES.get(), ModBlocks.FLOWERING_PEAR_LEAVES.get());
        event.register((state, level, pos, tintIndex) -> !state.getValue(FruitLogBlock.LEAFY) ? -1
                        : level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor(),
                ModBlocks.CACAO_LOG.get(), ModBlocks.CACAO_WOOD.get(), ModBlocks.STRIPPED_CACAO_LOG.get(), ModBlocks.STRIPPED_CACAO_WOOD.get(),
                ModBlocks.PEAR_LOG.get(), ModBlocks.PEAR_WOOD.get(), ModBlocks.STRIPPED_PEAR_LOG.get(), ModBlocks.STRIPPED_PEAR_WOOD.get());
    }

    private static int tubewormColor(DyeColor color) {
        return switch (color) {
            case BLACK -> 0x2f2332;
            case GRAY -> 0x62636e;
            case LIGHT_GRAY -> 0x9fb2b1;
            case WHITE -> 0xe1e5e5;
            case PINK -> 0xff87c5;
            case MAGENTA -> 0xee61e6;
            case PURPLE -> 0x8d4ad0;
            case BLUE -> 0x5163ee;
            case LIGHT_BLUE -> 0x6aadff;
            case CYAN -> 0x4ad3e3;
            case GREEN -> 0x409f42;
            case LIME -> 0x50ff52;
            case YELLOW -> 0xfbd734;
            case ORANGE -> 0xff930b;
            case RED -> 0xb82f35;
            case BROWN -> 0x9c5b39;
        };
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        Item livingBubbleEgg = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("sulfur_cube_spawn_egg"));
        if (livingBubbleEgg != Items.AIR) {
            event.register((stack, tintIndex) -> tintIndex == 0 ? 0xFFEFCFF2 : 0xFFC09FC3, livingBubbleEgg);
        }
        event.register((stack, tintIndex) -> tintIndex == 0 ? FoliageColor.getDefaultColor() : -1, ModItems.CACAO_LEAVES.get(), ModItems.FLOWERING_CACAO_LEAVES.get(),
                ModItems.PEAR_LEAVES.get(), ModItems.FLOWERING_PEAR_LEAVES.get());
        if (TideCompat.isLoaded()) {
            registerTideSpawnEggColors(event);
        }
        RuneClientEvents.registerColors(event);
    }

    private static void registerTideSpawnEggColors(RegisterColorHandlersEvent.Item event) {
        try (InputStream in = ClientModEvents.class.getResourceAsStream("/assets/spicedcider/tide_spawn_egg_colors.json")) {
            if (in == null) return;
            JsonObject colors = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : colors.entrySet()) {
                Item egg = BuiltInRegistries.ITEM.get(ResourceLocation.parse(entry.getKey()));
                if (egg == Items.AIR) continue;
                JsonArray pair = entry.getValue().getAsJsonArray();
                int base = 0xFF000000 | Integer.parseInt(pair.get(0).getAsString().substring(1), 16);
                int spots = 0xFF000000 | Integer.parseInt(pair.get(1).getAsString().substring(1), 16);
                event.register((stack, tintIndex) -> tintIndex == 0 ? base : spots, egg);
            }
        } catch (IOException e) {
            SpicedCider.LOGGER.error("Failed to read Tide spawn egg colors", e);
        }
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        NetherFogBanks.registerShader(event);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(SpicedCider.RANDOM_NAME_GENERATOR);
        event.registerReloadListener(new BrokenTextureReloadListener());
        CloudCoverClient.registerReloadListener(event);
        NetherHeatHaze.registerReloadListener(event);
    }

    @SubscribeEvent
    public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(PocketJukeboxTooltip.class, PocketJukeboxTooltipRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("controlify")) {
            ControlifySettings.register();
            if (ModList.get().isLoaded("nomansland")) CarvingWheelScreenProcessor.register();
        }
        if (ModList.get().isLoaded("f3backport")) {
            LodDebugEntry.register();
        }
        PonderCompat.register();
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPORARY_COBWEB.get(), RenderType.cutout());
            if (ModList.get().isLoaded("toil_and_trouble")) {
                ItemBlockRenderTypes.setRenderLayer(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("toil_and_trouble", "cauldron")),
                        type -> type == RenderType.cutout() || type == RenderType.translucent());
            }
            Stream.of(ModBlocks.CACAO_SAPLING, ModBlocks.POTTED_CACAO_SAPLING, ModBlocks.CACAO_POD, ModBlocks.PEAR_SAPLING, ModBlocks.POTTED_PEAR_SAPLING,
                    ModBlocks.HANGING_PEAR, ModBlocks.HANGING_AVOCADO).forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            Stream.of(ModBlocks.CACAO_LEAVES, ModBlocks.FLOWERING_CACAO_LEAVES, ModBlocks.CACAO_LOG, ModBlocks.CACAO_WOOD, ModBlocks.STRIPPED_CACAO_LOG,
                            ModBlocks.STRIPPED_CACAO_WOOD, ModBlocks.PEAR_LEAVES, ModBlocks.FLOWERING_PEAR_LEAVES, ModBlocks.PEAR_LOG, ModBlocks.PEAR_WOOD,
                            ModBlocks.STRIPPED_PEAR_LOG, ModBlocks.STRIPPED_PEAR_WOOD, ModBlocks.FLOWERING_AVOCADO_LEAVES, ModBlocks.AVOCADO_LOG,
                            ModBlocks.AVOCADO_WOOD, ModBlocks.STRIPPED_AVOCADO_LOG, ModBlocks.STRIPPED_AVOCADO_WOOD)
                    .forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutoutMipped()));
            Stream.of(ModBlocks.SMALL_PRISMARINE_BUD, ModBlocks.MEDIUM_PRISMARINE_BUD, ModBlocks.LARGE_PRISMARINE_BUD, ModBlocks.PRISMARINE_CLUSTER,
                    ModBlocks.SULFIDE_VENT, ModBlocks.CALCITE_VENT, ModBlocks.TUBEWORM, ModBlocks.CHROMATIC_BRAMBLE, ModBlocks.TALL_CHROMATIC_BRAMBLE,
                    ModBlocks.ACHROMATIC_BRAMBLE, ModBlocks.TALL_ACHROMATIC_BRAMBLE).forEach(block -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
            if (ModBlocks.TWISTED_SIGN != null) {
                Sheets.addWoodType(ModWoodTypes.TWISTED);
            }
            ItemProperties.register(ModItems.POCKET_JUKEBOX.get(), PLAYING, PocketJukeboxClient::playingProperty);
            ModItems.POCKET_JUKEBOXES.values().forEach(item -> ItemProperties.register(item.get(), PLAYING, PocketJukeboxClient::playingProperty));
            ItemProperties.register(Items.FILLED_MAP, MAP_ICON, MapIconProperty::get);
            if (ModItems.ADAMANTITE_FISHING_ROD != null) {
                ItemProperties.register(ModItems.ADAMANTITE_FISHING_ROD.get(), ResourceLocation.withDefaultNamespace("cast"), (stack, level, entity, seed) -> {
                    if (!(entity instanceof Player player) || player.fishing == null) return 0.0F;
                    boolean mainHand = player.getMainHandItem() == stack;
                    boolean offHand = player.getOffhandItem() == stack && !(player.getMainHandItem().getItem() instanceof FishingRodItem);
                    return mainHand || offHand ? 1.0F : 0.0F;
                });
            }
        });
    }

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        Path gameDir = FMLPaths.GAMEDIR.get();
        Path cacheDir = gameDir.resolve(".spicedcider_cache");
        Path manifestPath = FMLPaths.CONFIGDIR.get().resolve("spicedcider/spicedcider_manifest.json");
        Path resourcePacksDir = gameDir.resolve("resourcepacks");

        ResourceBaker.bakeFromManifest(cacheDir, manifestPath, resourcePacksDir);

        Path globalJitPath = cacheDir.resolve("spicedcider_global_jit.zip");
        if (!Files.exists(globalJitPath)) return;

        try {
            String packId = "spicedcider_global_jit";

            PackLocationInfo info = new PackLocationInfo(
                    packId,
                    Component.literal("Spiced Cider Global JIT"),
                    PackSource.BUILT_IN,
                    Optional.empty()
            );

            PackSelectionConfig selectionConfig = new PackSelectionConfig(false, Pack.Position.TOP, false);

            Pack pack = Pack.readMetaAndCreate(
                    info,
                    new FilePackResources.FileResourcesSupplier(globalJitPath),
                    PackType.CLIENT_RESOURCES,
                    selectionConfig
            );

            if (pack != null) {
                event.addRepositorySource(consumer -> consumer.accept(pack));
            }
        } catch (Exception e) {
            SpicedCider.LOGGER.error("Failed to inject Spiced Cider Global JIT pack", e);
        }
    }
}
