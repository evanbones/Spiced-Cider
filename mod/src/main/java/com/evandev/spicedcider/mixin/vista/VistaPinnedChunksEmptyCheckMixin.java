package com.evandev.spicedcider.mixin.vista;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.client.chunk_tracking.ClientPinnedChunksManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@IfModLoaded("vista")
@Mixin(value = ClientPinnedChunksManager.class, remap = false)
public class VistaPinnedChunksEmptyCheckMixin {

    @Unique
    private static volatile boolean spicedcider$everPinned;

    @WrapOperation(method = "pin", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object spicedcider$markPinned(Map<Object, Object> map, Object key, Object value, Operation<Object> original) {
        spicedcider$everPinned = true;
        return original.call(map, key, value);
    }

    @WrapOperation(method = {"get", "isPinned"}, at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z"))
    private static boolean spicedcider$skipCounterSum(Map<?, ?> map, Operation<Boolean> original) {
        return !spicedcider$everPinned;
    }
}
