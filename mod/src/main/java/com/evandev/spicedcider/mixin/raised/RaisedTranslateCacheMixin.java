package com.evandev.spicedcider.mixin.raised;

import com.evandev.spicedcider.compat.raised.RaisedTranslateCache;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@IfModLoaded("raised")
@Mixin(targets = "dev.yurisuika.raised.util.Translate", remap = false)
public class RaisedTranslateCacheMixin {

    @WrapMethod(method = "getX(Ljava/lang/String;)I")
    private static int spicedcider$cachedX(String layerName, Operation<Integer> original) {
        return RaisedTranslateCache.x(layerName, original);
    }

    @WrapMethod(method = "getY(Ljava/lang/String;)I")
    private static int spicedcider$cachedY(String layerName, Operation<Integer> original) {
        return RaisedTranslateCache.y(layerName, original);
    }
}
