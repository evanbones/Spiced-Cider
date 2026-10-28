package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.tears.WeepingTears;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class WeepingTearsMixin {

    @ModifyReturnValue(method = "isRandomlyTicking", at = @At("RETURN"))
    private boolean spicedcider$weep(boolean ticking) {
        return ticking || (Object) this instanceof Block block && WeepingTears.canWeep(block);
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void spicedcider$dripTears(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if ((Object) this instanceof Block block && WeepingTears.canWeep(block)) {
            WeepingTears.randomTick(state, level, pos, random);
        }
    }
}
