package com.evandev.spicedcider.mixin.lodged;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@IfModLoaded("lodged")
@Mixin(targets = "com.jvn.lodged.client.InventoryArrowTooltips", remap = false)
public class LodgedArrowTooltipMixin {

    @WrapOperation(
            method = "renderArrowRemovalTooltip(Lnet/minecraft/client/gui/GuiGraphics;Lcom/jvn/lodged/client/InventoryArrowTooltips$HoveredArrow;IID)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2)
    )
    private static boolean spicedcider$hideArrowDepth(List<Object> list, Object line, Operation<Boolean> original) {
        return !SpicedCiderConfig.CLIENT.lodgedHideArrowDepth.get() && original.call(list, line);
    }

    @WrapOperation(
            method = "renderArrowRemovalTooltip(Lnet/minecraft/client/gui/GuiGraphics;Lcom/jvn/lodged/client/InventoryArrowTooltips$HoveredArrow;IID)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 4)
    )
    private static boolean spicedcider$hideBleedingRisk(List<Object> list, Object line, Operation<Boolean> original) {
        return !SpicedCiderConfig.CLIENT.lodgedHideBleedingRisk.get() && original.call(list, line);
    }
}
