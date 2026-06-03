package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.renderer.WorkstoneRenderer;
import com.evandev.spicedcider.client.renderer.projectiles.CobwebProjectileRenderer;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModEntityTypes;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public class ClientModEvents {

    @EventBusSubscriber(modid = SpicedCider.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.WORKSTONE.get(), WorkstoneRenderer::new);
            event.registerEntityRenderer(ModEntityTypes.COBWEB_PROJECTILE.get(), CobwebProjectileRenderer::new);
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
    }
}