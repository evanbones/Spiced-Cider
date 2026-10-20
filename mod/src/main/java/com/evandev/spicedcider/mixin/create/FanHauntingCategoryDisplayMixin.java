package com.evandev.spicedcider.mixin.create;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@IfModLoaded("create")
@Mixin(targets = {"com.simibubi.create.compat.jei.category.ProcessingViaFanCategory", "com.simibubi.create.compat.jei.category.ProcessingViaFanCategory$MultiOutput"}, remap = false)
public class FanHauntingCategoryDisplayMixin {
    private static final String SPICEDCIDER$HAUNTING_CATEGORY = "com.simibubi.create.compat.jei.category.FanHauntingCategory";

    @ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/gui/element/GuiGameElement$GuiRenderBuilder;scale(D)Lnet/createmod/catnip/gui/element/GuiGameElement$GuiRenderBuilder;"), index = 0, require = 0)
    private double spicedcider$hideFan(double scale) {
        return SPICEDCIDER$HAUNTING_CATEGORY.equals(this.getClass().getName()) ? 0.0 : scale;
    }

    @ModifyArg(method = "renderWidgets", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/AllGuiTextures;render(Lnet/minecraft/client/gui/GuiGraphics;II)V", ordinal = 0), index = 1, require = 0)
    private int spicedcider$hideFanShadow(int x) {
        return SPICEDCIDER$HAUNTING_CATEGORY.equals(this.getClass().getName()) ? -10000 : x;
    }
}
