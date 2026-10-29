package com.evandev.spicedcider.mixin.perf;

import com.evandev.spicedcider.util.PackFileIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.nio.file.Path;

@Mixin(PathPackResources.class)
public class PathPackResourcesMixin {
    @Shadow
    @Final
    private Path root;

    @Unique
    private final PackFileIndex[] spicedcider$indexes = new PackFileIndex[PackType.values().length];

    @Inject(method = "getResource(Lnet/minecraft/server/packs/PackType;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/server/packs/resources/IoSupplier;", at = @At("HEAD"), cancellable = true)
    private void spicedcider$skipMissingResource(PackType type, ResourceLocation location, CallbackInfoReturnable<IoSupplier<InputStream>> cir) {
        PackFileIndex index = spicedcider$index(type);
        if (index != null && index.definitelyMissing(location.getNamespace(), location.getPath())) cir.setReturnValue(null);
    }

    @Inject(method = "listResources", at = @At("HEAD"), cancellable = true)
    private void spicedcider$skipMissingDirectory(PackType type, String namespace, String path, PackResources.ResourceOutput output, CallbackInfo ci) {
        PackFileIndex index = spicedcider$index(type);
        if (index != null && index.definitelyMissing(namespace, path)) ci.cancel();
    }

    @Unique
    private PackFileIndex spicedcider$index(PackType type) {
        if (root == null || !PackFileIndex.indexable(root)) return null;
        int slot = type.ordinal();
        PackFileIndex index = spicedcider$indexes[slot];
        if (index != null) return index;
        synchronized (spicedcider$indexes) {
            index = spicedcider$indexes[slot];
            if (index == null) {
                index = PackFileIndex.get(root, type);
                spicedcider$indexes[slot] = index;
            }
            return index;
        }
    }
}
