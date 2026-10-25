package com.evandev.spicedcider.mixin.continuity;

import com.evandev.spicedcider.compat.continuity.ContinuityLazyModelWrapping;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import me.pepperbell.continuity.client.ContinuityClientModEvents;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("continuity")
@IfModLoaded("modernfix")
@Mixin(value = ContinuityClientModEvents.class, remap = false)
public class ContinuityModelBakingMixin {

    @WrapMethod(method = "onModifyBakingResult")
    private static void spicedcider$wrapLazily(ModelEvent.ModifyBakingResult event, Operation<Void> original) {
        if (!ContinuityLazyModelWrapping.bakesLazily(event.getModelBakery())) original.call(event);
    }
}
