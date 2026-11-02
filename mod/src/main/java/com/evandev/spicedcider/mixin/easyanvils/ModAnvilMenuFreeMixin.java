package com.evandev.spicedcider.mixin.easyanvils;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("easyanvils")
@Mixin(targets = "fuzs.easyanvils.world.inventory.ModAnvilMenu", remap = false)
public class ModAnvilMenuFreeMixin {

    @Inject(method = "createResult()V", at = @At("RETURN"))
    private void spicedcider$free(CallbackInfo ci) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.easyAnvilsFreeRepairs, false)) {
            ((AnvilMenu) (Object) this).setMaximumCost(0);
        }
    }
}
