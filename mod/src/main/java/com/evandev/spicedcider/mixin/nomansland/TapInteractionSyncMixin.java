package com.evandev.spicedcider.mixin.nomansland;

import com.farcr.nomansland.common.event.RegistrySetupEvents;
import com.farcr.nomansland.common.registry.NMLRegistries;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nomansland")
@Mixin(value = RegistrySetupEvents.class, remap = false)
public class TapInteractionSyncMixin {

    @WrapOperation(method = "registerDatapackRegistries", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/registries/DataPackRegistryEvent$NewRegistry;dataPackRegistry(Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Codec;)V"))
    private static <T> void spicedcider$syncTapping(DataPackRegistryEvent.NewRegistry event, ResourceKey<Registry<T>> key, Codec<T> codec, Operation<Void> original) {
        if (key == (Object) NMLRegistries.TAP_INTERACTION_KEY) {
            event.dataPackRegistry(key, codec, codec);
        } else {
            original.call(event, key, codec);
        }
    }
}
