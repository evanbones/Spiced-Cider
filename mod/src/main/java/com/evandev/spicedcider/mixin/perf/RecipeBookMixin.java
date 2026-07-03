package com.evandev.spicedcider.mixin.perf;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.stats.RecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBook.class)
public class RecipeBookMixin {
    @Inject(method = "copyOverData", at = @At("HEAD"), cancellable = true)
    public void onCopy(RecipeBook pOther, CallbackInfo ci) {
        if (SpicedCiderConfig.COMMON.disableRecipeBookTracking.get()) {
            ci.cancel();
        }
    }
}