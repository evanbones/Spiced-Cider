package com.evandev.spicedcider.mixin.vista.accessor;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("xTransparentOld")
    void spicedcider$setXTransparentOld(double value);

    @Accessor("yTransparentOld")
    void spicedcider$setYTransparentOld(double value);

    @Accessor("zTransparentOld")
    void spicedcider$setZTransparentOld(double value);

    @Accessor("prevCloudX")
    void spicedcider$setPrevCloudX(int value);

    @Accessor("prevCloudY")
    void spicedcider$setPrevCloudY(int value);

    @Accessor("prevCloudZ")
    void spicedcider$setPrevCloudZ(int value);

    @Accessor("prevCloudColor")
    void spicedcider$setPrevCloudColor(Vec3 value);

    @Accessor("prevCloudsType")
    void spicedcider$setPrevCloudsType(CloudStatus value);

    @Accessor("generateClouds")
    void spicedcider$setGenerateClouds(boolean value);

    @Accessor("ticks")
    int spicedcider$getTicks();
}
