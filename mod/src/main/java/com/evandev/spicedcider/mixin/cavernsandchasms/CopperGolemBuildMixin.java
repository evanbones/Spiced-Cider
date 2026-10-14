package com.evandev.spicedcider.mixin.cavernsandchasms;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("caverns_and_chasms")
@Mixin(targets = "com.teamabnormals.caverns_and_chasms.common.entity.animal.CopperGolem", remap = false)
public class CopperGolemBuildMixin {

    @Inject(method = "createGolem", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$keepBlocks(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        ci.cancel();
    }
}
