package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRespawnLogic.class)
public class PlayerRespawnLogicNoLeavesMixin {

    @Inject(method = "getOverworldRespawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;isFaceFull(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/core/Direction;)Z"), cancellable = true)
    private static void spicedcider$rejectLeaves(ServerLevel level, int x, int z, CallbackInfoReturnable<BlockPos> cir, @Local BlockState state) {
        if (state.is(BlockTags.LEAVES) && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.noLeafSpawns, true)) {
            cir.setReturnValue(null);
        }
    }
}
