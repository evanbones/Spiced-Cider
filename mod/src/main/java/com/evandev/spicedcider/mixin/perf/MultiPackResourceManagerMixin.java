package com.evandev.spicedcider.mixin.perf;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.perf.NamespaceCache;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Set;

@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin {

    @Unique
    private static final Set<String> ESSENTIAL_NAMESPACES = Set.of(
            "minecraft", "realms", "optifine", "shaders", "sodium", "iris"
    );

    @WrapOperation(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/PackResources;getNamespaces(Lnet/minecraft/server/packs/PackType;)Ljava/util/Set;")
    )
    private Set<String> fastAssetsLoad$filterNamespaces(PackResources instance, PackType packType, Operation<Set<String>> original) {
        Set<String> originalNamespaces = original.call(instance, packType);

        if (packType != PackType.CLIENT_RESOURCES || !SpicedCiderConfig.STARTUP.filterUnusedResourcePackNamespaces.get()) {
            return originalNamespaces;
        }

        Set<String> filteredNamespaces = new HashSet<>();

        for (String namespace : originalNamespaces) {
            if (ESSENTIAL_NAMESPACES.contains(namespace) || NamespaceCache.VALID_NAMESPACES.contains(namespace)) {
                filteredNamespaces.add(namespace);
            } else {
                SpicedCider.LOGGER.debug("Blocked unloaded namespace: {} in pack {}", namespace, instance.packId());
            }
        }

        return filteredNamespaces;
    }
}