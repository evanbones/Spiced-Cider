package com.evandev.spicedcider.mixin.minecraft.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Camera.class)
public abstract class CameraWaterNearPlaneMixin {
    @Shadow
    private BlockGetter level;

    @Shadow
    private boolean initialized;

    @Shadow
    public abstract Vec3 getPosition();

    @Shadow
    public abstract Camera.NearPlane getNearPlane();

    @ModifyReturnValue(method = "getFluidInCamera", at = @At("RETURN"))
    private FogType spicedcider$waterAtNearPlane(FogType original) {
        if (original != FogType.NONE || !this.initialized || this.level == null) return original;
        Camera.NearPlane plane = this.getNearPlane();
        Vec3 position = this.getPosition();
        for (Vec3 offset : List.of(plane.getPointOnPlane(0.0F, 0.0F), plane.getTopLeft(), plane.getTopRight(), plane.getBottomLeft(), plane.getBottomRight())) {
            Vec3 point = position.add(offset);
            BlockPos pos = BlockPos.containing(point);
            FluidState fluid = this.level.getFluidState(pos);
            if (fluid.is(FluidTags.WATER) && point.y <= pos.getY() + fluid.getHeight(this.level, pos)) {
                return FogType.WATER;
            }
        }
        return original;
    }
}
