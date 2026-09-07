package com.evandev.spicedcider.mixin.clutternomore;

import com.evandev.spicedcider.compat.clutternomore.ClutterNoMoreReloadState;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@IfModLoaded("clutternomore")
@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerAutoRetryMixin {

    @Shadow
    @Final
    private PackType type;

    @Inject(method = "createReload", at = @At("RETURN"))
    private void spicedcider$retryAfterReload(
            Executor backgroundExecutor,
            Executor gameExecutor,
            CompletableFuture<Unit> waitingFor,
            List<PackResources> resourcePacks,
            CallbackInfoReturnable<ReloadInstance> cir
    ) {
        if (this.type != PackType.CLIENT_RESOURCES) return;

        ReloadInstance reload = cir.getReturnValue();
        reload.done().whenComplete((result, throwable) -> {
            if (throwable != null) return;
            if (ClutterNoMoreReloadState.consumeShouldRetry()) {
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().reloadResourcePacks());
            }
        });
    }
}
