package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.EdgeFogShaders;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("roxy")
@IfModLoaded("sodium")
@Mixin(ShaderInstance.class)
public abstract class CoreShaderEdgeFogUniformMixin {

    @Unique
    private int spicedcider$edgeFogLocation = -2;

    @Inject(method = "apply", at = @At("TAIL"))
    private void spicedcider$edgeFog(CallbackInfo ci) {
        if (this.spicedcider$edgeFogLocation == -2) {
            this.spicedcider$edgeFogLocation = GL20C.glGetUniformLocation(((ShaderInstance) (Object) this).getId(), EdgeFogShaders.CORE_UNIFORM);
        }
        if (this.spicedcider$edgeFogLocation >= 0) EdgeFogShaders.bindCore(this.spicedcider$edgeFogLocation);
    }
}
