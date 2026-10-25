package com.evandev.spicedcider.mixin.leaks;

import com.evandev.spicedcider.util.ClientCacheRegistry;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("ambientsounds")
@Mixin(targets = "team.creative.ambientsounds.environment.EntityEnvironment", remap = false)
public class AmbientSoundsEntityEnvironmentMixin {
    @Shadow
    private List<Entity> entities;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$track(CallbackInfo ci) {
        ClientCacheRegistry.track(entities);
    }
}
