package com.evandev.spicedcider.mixin.tide;

import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.evandev.spicedcider.compat.tide.TideHookEffects;
import com.li64.tide.data.fishing.CatchResult;
import com.li64.tide.data.fishing.FishData;
import com.li64.tide.data.fishing.FishingContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("tide")
@Mixin(value = FishData.class, remap = false)
public class FishDataMixin {

    @WrapMethod(method = "shouldKeep")
    private boolean spicedcider$skipRemovedFish(FishingContext context, Operation<Boolean> original) {
        if (ReliableRemoverCompat.isRemoved(((FishData) (Object) this).fish().value())) return false;
        return original.call(context);
    }

    @WrapMethod(method = "weight")
    private double spicedcider$heavyHookWeight(FishingContext context, Operation<Double> original) {
        return TideHookEffects.weight(context.hook(), (FishData) (Object) this, original.call(context));
    }

    @WrapMethod(method = "getResult")
    private CatchResult spicedcider$doubleHookCatch(FishingContext context, Operation<CatchResult> original) {
        CatchResult result = original.call(context);
        if (TideHookEffects.doubleCatch(context.hook())) {
            result.items().forEach(stack -> stack.grow(1));
        }
        return result;
    }
}
