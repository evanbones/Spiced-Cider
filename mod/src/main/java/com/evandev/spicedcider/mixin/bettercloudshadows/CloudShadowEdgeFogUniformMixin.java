package com.evandev.spicedcider.mixin.bettercloudshadows;

import com.evandev.spicedcider.compat.voxy.EdgeFogShaders;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("better_cloud_shadows")
@Mixin(ShaderInstance.class)
public abstract class CloudShadowEdgeFogUniformMixin {

    @Inject(method = "apply", at = @At("TAIL"))
    private void spicedcider$edgeFog(CallbackInfo ci) {
        ShaderInstance shader = (ShaderInstance) (Object) this;
        if (EdgeFogShaders.CLOUD_SHADOW_PROGRAM.equals(shader.getName())) {
            EdgeFogShaders.bindCloudShadow(shader.getId());
        }
    }
}
