package com.evandev.spicedcider.mixin.wherewindsblow;

import com.evandev.spicedcider.compat.wherewindsblow.WhereWindsBlowLazyModelWrapping;
import com.jvn.wherewindsblow.client.WhereWindsBlowClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("where_winds_blow")
@IfModLoaded("modernfix")
@Mixin(value = WhereWindsBlowClient.class, remap = false)
public class WhereWindsBlowClientBakingMixin {

    @WrapMethod(method = "modifyBakedModels")
    private static void spicedcider$wrapLazily(ModelEvent.ModifyBakingResult event, Operation<Void> original) {
        if (!WhereWindsBlowLazyModelWrapping.bakesLazily(event.getModelBakery())) original.call(event);
    }
}
