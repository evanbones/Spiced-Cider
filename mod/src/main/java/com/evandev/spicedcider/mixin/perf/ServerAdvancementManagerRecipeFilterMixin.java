package com.evandev.spicedcider.mixin.perf;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerRecipeFilterMixin {

    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;buildOrThrow()Lcom/google/common/collect/ImmutableMap;"))
    private ImmutableMap<ResourceLocation, AdvancementHolder> spicedcider$dropRecipeUnlocks(ImmutableMap.Builder<ResourceLocation, AdvancementHolder> builder, Operation<ImmutableMap<ResourceLocation, AdvancementHolder>> original) {
        ImmutableMap<ResourceLocation, AdvancementHolder> advancements = original.call(builder);
        if (!SpicedCiderConfig.COMMON.disableRecipeBookTracking.get()) return advancements;
        ImmutableMap.Builder<ResourceLocation, AdvancementHolder> kept = ImmutableMap.builder();
        advancements.forEach((id, holder) -> {
            if (!cider$isRecipeUnlock(id, holder.value())) kept.put(id, holder);
        });
        return kept.buildOrThrow();
    }

    @Unique
    private static boolean cider$isRecipeUnlock(ResourceLocation id, Advancement advancement) {
        if (!id.getPath().startsWith("recipes/") || advancement.display().isPresent()) return false;
        AdvancementRewards rewards = advancement.rewards();
        return !rewards.recipes().isEmpty() && rewards.loot().isEmpty() && rewards.experience() == 0 && rewards.function().isEmpty();
    }
}
