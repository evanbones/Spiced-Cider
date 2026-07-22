package com.evandev.spicedcider.mixin.spelunkery;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(PathPackResources.class)
public class SpelunkeryMoresFixMixin {

    @Inject(
            method = "getResource(Lnet/minecraft/server/packs/PackType;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/server/packs/resources/IoSupplier;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void spicedcider$suppressSpelunkeryStaticWorldgen(
            PackType type,
            ResourceLocation location,
            CallbackInfoReturnable<IoSupplier<InputStream>> cir
    ) {
        if (type == PackType.SERVER_DATA && location != null) {
            PathPackResources pack = (PathPackResources) (Object) this;
            String packId = pack.location() != null ? pack.location().id() : null;
            if (packId != null && packId.contains("spelunkery") && !packId.contains("generated_pack")) {
                String path = location.getPath();
                if (path.startsWith("worldgen/configured_feature/")) {
                    String ns = location.getNamespace();
                    if ("minecraft".equals(ns) || "oreganized".equals(ns) || "sullysmod".equals(ns) || "create".equals(ns)) {
                        cir.setReturnValue(null);
                    }
                }
            }
        }
    }
}
