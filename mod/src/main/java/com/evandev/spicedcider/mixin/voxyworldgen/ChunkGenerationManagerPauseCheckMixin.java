package com.evandev.spicedcider.mixin.voxyworldgen;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.ethan.voxyworldgenv2.core.ChunkGenerationManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BooleanSupplier;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = ChunkGenerationManager.class, remap = false)
public class ChunkGenerationManagerPauseCheckMixin {

    @WrapOperation(
            method = "initialize",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/ethan/voxyworldgenv2/core/ChunkGenerationManager;pauseCheck:Ljava/util/function/BooleanSupplier;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void spicedcider$keepRegisteredPauseCheck(ChunkGenerationManager instance, BooleanSupplier value, Operation<Void> original) {
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.voxyWorldGenPauseFix, true)) {
            original.call(instance, value);
        }
    }
}
