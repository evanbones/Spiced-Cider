package com.evandev.spicedcider.mixin.roxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.common.world.other.Mapper", remap = false)
public class VoxyMapperAirLogMixin {

    @WrapOperation(method = "loadFromStorage", at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/Logger;error([Ljava/lang/Object;)V"))
    private void spicedcider$quietRemovedBlocks(Object[] args, Operation<Void> original) {
        if (args.length == 1 && "Deserialization was air, removed block".equals(args[0])) {
            return;
        }
        original.call((Object) args);
    }
}
