package com.evandev.spicedcider.mixin.roxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.common.world.other.Mapper$StateEntry", remap = false)
public class VoxyStateEntryDecodeLogMixin {

    @Unique
    private static final Logger spicedcider$LOGGER = LogUtils.getLogger();

    @WrapOperation(method = "deserialize", at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/Logger;error([Ljava/lang/Object;)V"))
    private static void spicedcider$quietRemovedBlocks(Object[] args, Operation<Void> original) {
        if (args.length == 1 && args[0] instanceof String message && message.startsWith("Could not decode blockstate setting to air")) {
            spicedcider$LOGGER.warn("[Voxy] {}", message);
            return;
        }
        original.call((Object) args);
    }
}
