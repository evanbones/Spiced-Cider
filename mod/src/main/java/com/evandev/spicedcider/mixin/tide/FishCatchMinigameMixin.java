package com.evandev.spicedcider.mixin.tide;

import com.evandev.spicedcider.compat.tide.TideHookEffects;
import com.li64.tide.data.minigame.FishCatchMinigame;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@IfModLoaded("tide")
@Mixin(value = FishCatchMinigame.class, remap = false)
public abstract class FishCatchMinigameMixin {

    @Shadow
    private TideFishingHook hook;

    @Shadow
    public abstract void onWin(boolean perfectCatch);

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/li64/tide/network/messages/MinigameClientMsg;<init>(BBBFF)V"), index = 3)
    private float spicedcider$hookArea(float area) {
        return TideHookEffects.minigameArea(this.hook, area);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/li64/tide/network/messages/MinigameClientMsg;<init>(BBBFF)V"), index = 4)
    private float spicedcider$hookSpeed(float speed) {
        return TideHookEffects.minigameSpeed(this.hook, speed);
    }

    @WrapMethod(method = "onFail")
    private void spicedcider$featherlightFail(Operation<Void> original) {
        if (TideHookEffects.keepsFish(this.hook)) this.onWin(false);
        else original.call();
    }

    @WrapMethod(method = "onTimeout")
    private void spicedcider$featherlightTimeout(Operation<Void> original) {
        if (TideHookEffects.keepsFish(this.hook)) this.onWin(false);
        else original.call();
    }
}
