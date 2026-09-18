package com.evandev.spicedcider.mixin.perf;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @WrapOperation(method = "updateRegistryTags()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;rebuildCache()V"))
    private void rebuildBlockCache(Operation<Void> original) {
        // Block shape properties rebuilt here don't depend on tags, so this is safe to skip
        if (!SpicedCiderConfig.COMMON.skipRedundantBlockCacheRebuild.get()) {
            original.call();
        }
    }
}