package com.evandev.spicedcider.mixin.nexuslib;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nexuslib")
@Mixin(targets = "net.hecco.nexuslib.lib.util.NLCapeManager", remap = false)
public class CapeManagerServerSaveMixin {

    @WrapOperation(method = "setSelected", at = @At(value = "INVOKE", target = "Lnet/hecco/nexuslib/lib/util/NLCapeManager;save()V"))
    private static void spicedcider$skipClientSaveOnServer(Operation<Void> original) {
        if (FMLEnvironment.dist.isDedicatedServer()) return;
        original.call();
    }
}
