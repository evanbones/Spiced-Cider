package com.evandev.spicedcider.mixin.chalk;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@IfModLoaded("chalk")
@Mixin(targets = "io.github.mortuusars.chalk.event.CommonEvents", remap = false)
public class SymbolUnlockedActionBarMixin {

    @ModifyArg(
            method = {"onAdvancementAward", "lambda$onAdvancementAward$2"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V"),
            index = 1,
            require = 0
    )
    private static boolean spicedcider$showAboveHotbar(boolean actionBar) {
        return actionBar || SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.chalkSymbolsAboveHotbar, false);
    }
}
