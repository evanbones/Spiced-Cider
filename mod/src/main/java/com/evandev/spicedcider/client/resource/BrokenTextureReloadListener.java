package com.evandev.spicedcider.client.resource;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BrokenTextureReloadListener implements PreparableReloadListener {

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager resourceManager,
                                           ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler,
                                           Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> findMissingBrokenTextures(resourceManager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(BrokenTextureReloadListener::logMissing, gameExecutor);
    }

    private static List<ResourceLocation> findMissingBrokenTextures(ResourceManager resourceManager) {
        List<ResourceLocation> missing = new ArrayList<>();

        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack sample = new ItemStack(item);
            if (!sample.isDamageableItem()) continue;
            if (!sample.is(ModTags.Items.KEPT_WHEN_BROKEN) || sample.is(ModTags.Items.DESTROYED_WHEN_BROKEN)) continue;

            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/item/" + id.getPath() + "_broken.png");

            if (resourceManager.getResource(textureLoc).isEmpty()) {
                missing.add(id);
            }
        }

        return missing;
    }

    private static void logMissing(List<ResourceLocation> missing) {
        if (missing.isEmpty()) return;

        SpicedCider.LOGGER.warn("{} tool(s) are missing a _broken texture and will fall back to their normal texture when broken: {}",
                missing.size(), missing);
    }
}
