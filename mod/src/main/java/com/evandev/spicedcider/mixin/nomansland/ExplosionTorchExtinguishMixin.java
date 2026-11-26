package com.evandev.spicedcider.mixin.nomansland;

import com.farcr.nomansland.NMLConfig;
import com.farcr.nomansland.common.event.ExplosionEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nomansland")
@Mixin(value = ExplosionEvents.class, remap = false)
public class ExplosionTorchExtinguishMixin {

    @WrapOperation(method = "onExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean spicedcider$noExplosionExtinguish(BlockState state, Block block, Operation<Boolean> original) {
        return NMLConfig.TORCH_EXTINGUISHING.get() && original.call(state, block);
    }
}
