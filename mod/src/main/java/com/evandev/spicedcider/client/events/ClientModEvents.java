package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.models.projectile.CobwebProjectileModel;
import com.evandev.spicedcider.client.renderer.WorkstoneRenderer;
import com.evandev.spicedcider.client.renderer.projectiles.CobwebProjectileRenderer;
import com.evandev.spicedcider.client.renderer.projectiles.GrapplingHookRenderer;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModEntityTypes;
import com.evandev.spicedcider.registry.ModModelLayers;
import com.evandev.spicedcider.resource.ResourceBaker;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.WORKSTONE.get(), WorkstoneRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.COBWEB_PROJECTILE.get(), CobwebProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.GRAPPLING_HOOK.get(), GrapplingHookRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.COBWEB_PROJECTILE, CobwebProjectileModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(SpicedCider.RANDOM_NAME_GENERATOR);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPORARY_COBWEB.get(), RenderType.cutout());
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
