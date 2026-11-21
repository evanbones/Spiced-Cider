package com.evandev.spicedcider.mixin.fieldguide;

import com.evandev.spicedcider.compat.snowrealmagic.SnowContainedState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("fieldguide")
@IfModLoaded("snowrealmagic")
@Mixin(targets = "com.evandev.fieldguide.client.scan.manager.FieldGuideRaytracer", remap = false)
public class FieldGuideRaytracerSnowMixin {

    @WrapOperation(
            method = "processScanning",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", remap = true)
    )
    private BlockState spicedcider$scanPlantUnderSnow(ClientLevel level, BlockPos pos, Operation<BlockState> original) {
        return SnowContainedState.unwrap(level, pos, original.call(level, pos));
    }
}
