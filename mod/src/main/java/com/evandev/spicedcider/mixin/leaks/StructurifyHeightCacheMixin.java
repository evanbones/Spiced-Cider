package com.evandev.spicedcider.mixin.leaks;

import com.evandev.spicedcider.util.ThreadCacheRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Pseudo
@IfModLoaded("structurify")
@Mixin(targets = "com.faboslav.structurify.common.world.level.chunk.ChunkGeneratorHeightCache", remap = false)
public class StructurifyHeightCacheMixin {

    @WrapOperation(method = {"get", "put"}, at = @At(value = "INVOKE", target = "Ljava/lang/ThreadLocal;get()Ljava/lang/Object;"), remap = false)
    private static Object spicedcider$trackThreadCache(ThreadLocal<?> threadLocal, Operation<Object> original) {
        Object cache = original.call(threadLocal);
        if (cache instanceof Map<?, ?> map) ThreadCacheRegistry.track(map);
        return cache;
    }
}
