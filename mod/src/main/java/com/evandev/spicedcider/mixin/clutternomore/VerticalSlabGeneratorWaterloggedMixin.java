package com.evandev.spicedcider.mixin.clutternomore;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.client.assets.VerticalSlabGenerator;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("clutternomore")
@Mixin(value = VerticalSlabGenerator.class, remap = false)
public abstract class VerticalSlabGeneratorWaterloggedMixin {

    @WrapOperation(
            method = "lambda$generateBlock$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V"
            )
    )
    private static void spicedcider$addWaterloggedVariants(JsonObject variants, String property, JsonElement value, Operation<Void> original) {
        VerticalSlabBlock.WATERLOGGED.getAllValues().forEach(waterloggedValue -> original.call(variants, property + "," + waterloggedValue, value));
    }
}
