package com.evandev.spicedcider.mixin.picturemode;

import com.evandev.spicedcider.compat.picturemode.PictureModeBridge;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("picturemode")
@IfModLoaded("sodium")
@Mixin(value = RenderSectionManager.class, remap = false)
public class SodiumPictureModeGraphMixin {

    @ModifyReturnValue(method = "isOutOfGraph", at = @At("RETURN"))
    private boolean spicedcider$walkAllSectionsInPictureMode(boolean outOfGraph) {
        return outOfGraph || PictureModeBridge.enabled();
    }
}
