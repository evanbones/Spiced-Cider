package com.evandev.spicedcider.mixin.roxy;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@IfModLoaded("voxy")
@Mixin(targets = "me.cortex.voxy.common.world.WorldEngine", remap = false)
public class VoxySectionCacheMixin {

    @Unique
    private static final long SMALL_HEAP = 6L << 30;

    @Unique
    private static final int SMALL_HEAP_SECTIONS = 512;

    @ModifyArg(method = "<init>(Lme/cortex/voxy/common/config/section/SectionStorage;Lme/cortex/voxy/commonImpl/VoxyInstance;)V",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/world/ActiveSectionTracker;<init>(ILme/cortex/voxy/common/world/ActiveSectionTracker$SectionLoader;ILme/cortex/voxy/common/world/WorldEngine;)V"),
            index = 2)
    private int spicedcider$smallHeapCache(int sections) {
        return Runtime.getRuntime().maxMemory() < SMALL_HEAP ? Math.min(sections, SMALL_HEAP_SECTIONS) : sections;
    }
}
