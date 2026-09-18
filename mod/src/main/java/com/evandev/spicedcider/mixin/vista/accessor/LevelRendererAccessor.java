package com.evandev.spicedcider.mixin.vista.accessor;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("vista")
@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("xTransparentOld")
    double spicedcider$getXTransparentOld();

    @Accessor("xTransparentOld")
    void spicedcider$setXTransparentOld(double value);

    @Accessor("yTransparentOld")
    double spicedcider$getYTransparentOld();

    @Accessor("yTransparentOld")
    void spicedcider$setYTransparentOld(double value);

    @Accessor("zTransparentOld")
    double spicedcider$getZTransparentOld();

    @Accessor("zTransparentOld")
    void spicedcider$setZTransparentOld(double value);
}
