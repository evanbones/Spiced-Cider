package com.evandev.spicedcider.mixin.clutternomore;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.client.assets.StepGenerator;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("clutternomore")
@Mixin(value = StepGenerator.class, remap = false)
public abstract class StepGeneratorWaterloggedMixin {

    @WrapOperation(
            method = "lambda$generateBlock$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V"
            )
    )
    private static void spicedcider$addWaterloggedVariants(JsonObject variants, String property, JsonElement value, Operation<Void> original) {
        StepBlock.WATERLOGGED.getAllValues().forEach(waterloggedValue -> original.call(variants, property + "," + waterloggedValue, value));
    }
}
