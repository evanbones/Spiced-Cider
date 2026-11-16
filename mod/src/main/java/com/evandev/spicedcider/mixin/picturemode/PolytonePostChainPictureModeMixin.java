package com.evandev.spicedcider.mixin.picturemode;

import com.evandev.spicedcider.compat.picturemode.PictureModeBridge;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.polytone.content.shaders.post.PostChainEffect;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("picturemode")
@IfModLoaded("polytone")
@Mixin(value = PostChainEffect.class, remap = false)
public class PolytonePostChainPictureModeMixin {

    @Shadow
    @Final
    private ResourceLocation postChain;

    @ModifyReturnValue(method = "shouldBeOn", at = @At("RETURN"))
    private boolean spicedcider$offInPictureMode(boolean on) {
        return on && !("sunbathing".equals(postChain.getNamespace()) && PictureModeBridge.enabled());
    }
}
