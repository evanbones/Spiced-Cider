package com.evandev.spicedcider.mixin.mixedlitter;

import com.evandev.spicedcider.compat.mixedlitter.ResolvedActionCache;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.actions.VariantActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("mixed_litter")
@Mixin(value = VariantUtil.class, remap = false)
public class VariantUtilResolveCacheMixin {

    @WrapOperation(
            method = {"resolveTexture", "findAction"},
            at = @At(value = "INVOKE",
                    target = "Ldev/tazer/mixed_litter/actions/VariantActionType;resolve(Lcom/google/gson/JsonObject;Lcom/google/gson/JsonObject;Lcom/google/gson/JsonObject;)Ldev/tazer/mixed_litter/actions/VariantActionType;")
    )
    private static VariantActionType spicedcider$reuseResolvedAction(VariantActionType type, JsonObject actionArgs, JsonObject variantArgs,
                                                                     JsonObject defaults, Operation<VariantActionType> original) {
        return ResolvedActionCache.resolve(type, actionArgs, variantArgs, defaults, original);
    }

    @WrapMethod(method = "invalidateCaches")
    private static void spicedcider$clearResolvedActions(Operation<Void> original) {
        original.call();
        ResolvedActionCache.clear();
    }
}
