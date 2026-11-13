package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.content.molten.MoltenFluidType;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class CameraMoltenFogMixin {

    @ModifyReturnValue(method = "getFluidInCamera", at = @At("RETURN"))
    private FogType spicedcider$moltenMetalFog(FogType original) {
        if (original != FogType.NONE) return original;
        Camera camera = (Camera) (Object) this;
        if (camera.getEntity() == null) return original;
        Level level = camera.getEntity().level();
        BlockPos pos = camera.getBlockPosition();
        FluidState state = level.getFluidState(pos);
        if (state.getFluidType() instanceof MoltenFluidType && camera.getPosition().y < pos.getY() + state.getHeight(level, pos)) {
            return FogType.LAVA;
        }
        return original;
    }
}
