package com.evandev.spicedcider.mixin.moonlight;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicResourcePack;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicResourcesProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.SimplePackProvider;
import net.mehvahdjukaar.moonlight.core.MoonlightClient;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("moonlight")
@Mixin(value = MoonlightClient.class, remap = false)
public class MergedPackRegistrationMixin {

    @WrapMethod(method = "mergePackSupplier")
    private static SimplePackProvider spicedcider$lockMerge(DynamicResourcesProvider provider, Operation<SimplePackProvider> original) {
        synchronized (MoonlightClient.class) {
            return original.call(provider);
        }
    }

    @WrapMethod(method = "maybeMergeLegacyPack")
    private static boolean spicedcider$lockLegacyMerge(DynamicResourcePack pack, Operation<Boolean> original) {
        synchronized (MoonlightClient.class) {
            return original.call(pack);
        }
    }
}
