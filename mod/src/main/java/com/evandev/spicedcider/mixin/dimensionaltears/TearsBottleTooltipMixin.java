package com.evandev.spicedcider.mixin.dimensionaltears;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@IfModLoaded("dimensional_tears")
@Mixin(targets = "com.ordana.dimensional_tears.items.DimensionalTearsBottleItem", remap = false)
public class TearsBottleTooltipMixin {

    @WrapOperation(method = "appendHoverText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private boolean spicedcider$skipRhyme(List<Object> tooltip, Object line, Operation<Boolean> original) {
        return false;
    }
}
