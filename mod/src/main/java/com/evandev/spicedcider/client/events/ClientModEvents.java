package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.models.armor.MischiefArmorModel;
import com.evandev.spicedcider.client.models.projectile.CobwebProjectileModel;
import com.evandev.spicedcider.client.particle.CleaverSweepParticle;
import com.evandev.spicedcider.client.resource.BrokenTextureReloadListener;
import com.evandev.spicedcider.client.renderer.balloon.BalloonRenderer;
import com.evandev.spicedcider.client.renderer.projectiles.CobwebProjectileRenderer;
import com.evandev.spicedcider.client.renderer.projectiles.GrapplingHookRenderer;
import com.evandev.spicedcider.compat.tide.TideCompat;
import com.evandev.spicedcider.registry.*;
import com.evandev.spicedcider.resource.ResourceBaker;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import com.evandev.spicedcider.compat.controlify.ControlifySettings;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.COBWEB_PROJECTILE.get(), CobwebProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BALLOON.get(), BalloonRenderer::new);
        if (TideCompat.isLoaded()) {
            event.registerEntityRenderer(ModEntityTypes.GRAPPLING_HOOK.get(), GrapplingHookRenderer::new);
        }
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.COBWEB_PROJECTILE, CobwebProjectileModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.BALLOON, BalloonRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.CLEAVER_SWEEP.get(), CleaverSweepParticle.Factory::new);
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        Item livingBubbleEgg = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("sulfur_cube_spawn_egg"));
        if (livingBubbleEgg != Items.AIR) {
            event.register((stack, tintIndex) -> tintIndex == 0 ? 0xFFEFCFF2 : 0xFFC09FC3, livingBubbleEgg);
        }
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(SpicedCider.RANDOM_NAME_GENERATOR);
        event.registerReloadListener(new BrokenTextureReloadListener());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("controlify")) {
            ControlifySettings.register();
        }
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPORARY_COBWEB.get(), RenderType.cutout());
            if (ModBlocks.TWISTED_SIGN != null) {
                Sheets.addWoodType(ModWoodTypes.TWISTED);
            }
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
