package com.evandev.spicedcider.mixin.raised;

import com.evandev.spicedcider.compat.raised.RaisedTranslateCache;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import java.util.function.Consumer;

@Pseudo
@IfModLoaded("raised")
@Mixin(targets = "dev.yurisuika.raised.config.Config", remap = false)
public class RaisedConfigUpdateMixin {

    @WrapMethod(method = "update(Ljava/util/function/Consumer;)V")
    private static void spicedcider$invalidateTranslateCache(Consumer<?> updater, Operation<Void> original) {
        try {
            original.call(updater);
        } finally {
            RaisedTranslateCache.invalidate();
        }
    }
}
