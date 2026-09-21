package com.evandev.spicedcider.mixin.clutternomore;

import com.evandev.spicedcider.compat.clutternomore.ClutterNoMoreReloadState;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import net.mehvahdjukaar.moonlight.api.misc.IProgressTracker;
import net.mehvahdjukaar.moonlight.core.misc.ReloadInstanceWrapper;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("clutternomore")
@IfModLoaded("moonlight")
@Mixin(value = ReloadInstanceWrapper.class, remap = false)
public abstract class ReloadInstanceWrapperRegenerateMixin {

    @Inject(method = "executeEarlyReloadBlocking", at = @At("RETURN"))
    private static void spicedcider$regenerateClutterNoMoreAssets(PackType type, ResourceManager manager, IProgressTracker progress, CallbackInfo ci) {
        if (type != PackType.CLIENT_RESOURCES) return;
        ClutterNoMoreReloadState.reset();
        AssetGenerator.generate(manager);
    }
}
