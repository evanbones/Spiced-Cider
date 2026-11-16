package com.evandev.spicedcider.mixin.treeliable;

import com.evandev.spicedcider.compat.treeliable.LeafFelling;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@IfModLoaded("treeliable")
@Mixin(targets = "com.evandev.treeliable.common.chop.FellTreeResult", remap = false)
public class FellTreeResultLeafSticksMixin {

    @WrapOperation(method = "lambda$breakLeaves$4", at = @At(value = "INVOKE", target = "Lcom/evandev/treeliable/common/chop/FellTreeResult;decayLeavesInsteadOfBreaking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private static void spicedcider$noSticksOnDecay(ServerLevel level, BlockPos pos, BlockState state, Operation<Void> original) {
        LeafFelling.run(() -> original.call(level, pos, state));
    }

    @WrapOperation(method = "lambda$breakLeaves$4", at = @At(value = "INVOKE", target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"))
    private static void spicedcider$noSticksOnBreak(BiConsumer<Object, Object> breaker, Object pos, Object drop, Operation<Void> original) {
        LeafFelling.run(() -> original.call(breaker, pos, drop));
    }
}
