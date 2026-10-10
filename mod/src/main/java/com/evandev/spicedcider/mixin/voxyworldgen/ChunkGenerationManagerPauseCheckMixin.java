package com.evandev.spicedcider.mixin.voxyworldgen;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.ethan.voxyworldgenv2.core.ChunkGenerationManager;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = ChunkGenerationManager.class, remap = false)
public class ChunkGenerationManagerPauseCheckMixin {

    @Shadow
    private BooleanSupplier pauseCheck;

    @Redirect(
            method = "initialize",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/ethan/voxyworldgenv2/core/ChunkGenerationManager;pauseCheck:Ljava/util/function/BooleanSupplier;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void spicedcider$keepRegisteredPauseCheck(ChunkGenerationManager instance, BooleanSupplier value) {
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.voxyWorldGenPauseFix, true)) {
            this.pauseCheck = value;
        }
    }
}
