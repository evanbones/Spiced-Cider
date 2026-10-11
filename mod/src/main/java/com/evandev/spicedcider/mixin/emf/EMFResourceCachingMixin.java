package com.evandev.spicedcider.mixin.emf;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Optional;

@Pseudo
@IfModLoaded("entity_model_features")
@Mixin(targets = "traben.entity_model_features.utils.EMFResourceCaching", remap = false)
public class EMFResourceCachingMixin {
    @Shadow
    @Final
    public static Map<String, Boolean> RESOURCE_EXISTENCE_CACHE;

    @WrapOperation(method = "resourceExists", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceManager;getResource(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;"))
    private static Optional<Resource> spicedcider$skipScannedMisses(ResourceManager resources, ResourceLocation location, Operation<Optional<Resource>> original) {
        String path = location.getPath();
        if ((path.startsWith("emf/cem/") || path.startsWith("optifine/cem/")) && RESOURCE_EXISTENCE_CACHE.get(location.toString()) == null) {
            return Optional.empty();
        }
        return original.call(resources, location);
    }
}
