package com.evandev.spicedcider.mixin.perf;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Redirect(method = "updateRegistryTags()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;rebuildCache()V"))
    private void rebuildBlockCache() {
        // Block shape properties rebuilt here don't depend on tags, so this is safe to skip
        if (!SpicedCiderConfig.COMMON.skipRedundantBlockCacheRebuild.get()) {
            Blocks.rebuildCache();
        }
    }
}