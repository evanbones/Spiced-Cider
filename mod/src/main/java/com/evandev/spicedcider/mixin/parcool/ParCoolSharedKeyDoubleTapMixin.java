package com.evandev.spicedcider.mixin.parcool;

import com.evandev.spicedcider.compat.parcool.SharedKeyDoubleTap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("parcool")
@Mixin(targets = "com.alrex.parcool.client.input.ParCoolKeyBinds$InputState", remap = false)
public class ParCoolSharedKeyDoubleTapMixin {

    @WrapOperation(method = "update(Lnet/minecraft/client/KeyMapping;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
    private boolean spicedcider$doubleTapSharedKeys(KeyMapping key, Operation<Boolean> original) {
        return SharedKeyDoubleTap.gate(key, original.call(key));
    }
}
