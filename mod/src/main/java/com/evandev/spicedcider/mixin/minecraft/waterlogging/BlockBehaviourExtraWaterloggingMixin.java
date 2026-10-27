package com.evandev.spicedcider.mixin.minecraft.waterlogging;

import com.evandev.spicedcider.content.waterlogging.ExtraWaterlogging;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourExtraWaterloggingMixin {

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void spicedcider$waterloggedFluid(BlockState state, CallbackInfoReturnable<FluidState> cir) {
        if (ExtraWaterlogging.isWaterlogged(state)) cir.setReturnValue(Fluids.WATER.getSource(false));
    }
}
