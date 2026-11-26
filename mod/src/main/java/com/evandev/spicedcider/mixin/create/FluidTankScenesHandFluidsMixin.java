package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.FluidTankScenes;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.InputElementBuilder;
import net.createmod.ponder.api.element.TextElementBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("create")
@Mixin(value = FluidTankScenes.class, remap = false)
public class FluidTankScenesHandFluidsMixin {

    private static boolean spicedcider$enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.createTankHandFluids, false);
    }

    @WrapOperation(method = "storage", at = @At(value = "INVOKE", target = "Lnet/createmod/ponder/api/element/InputElementBuilder;showing(Lnet/createmod/catnip/gui/element/ScreenElement;)Lnet/createmod/ponder/api/element/InputElementBuilder;"))
    private static InputElementBuilder spicedcider$bucketWorks(InputElementBuilder builder, ScreenElement icon, Operation<InputElementBuilder> original) {
        return spicedcider$enabled() ? builder.rightClick() : original.call(builder, icon);
    }

    @WrapOperation(method = "storage", at = @At(value = "INVOKE", target = "Lnet/createmod/ponder/api/element/TextElementBuilder;colored(Lnet/createmod/ponder/api/PonderPalette;)Lnet/createmod/ponder/api/element/TextElementBuilder;", ordinal = 0))
    private static TextElementBuilder spicedcider$greenText(TextElementBuilder builder, PonderPalette palette, Operation<TextElementBuilder> original) {
        return original.call(builder, spicedcider$enabled() && palette == PonderPalette.RED ? PonderPalette.GREEN : palette);
    }
}
