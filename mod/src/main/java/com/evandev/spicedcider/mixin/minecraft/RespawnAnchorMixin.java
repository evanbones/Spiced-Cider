package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RespawnAnchorBlock.class)
public abstract class RespawnAnchorMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void spicedcider$preventAnchorExplosion(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!SpicedCiderConfig.COMMON.respawnAnchorExplosionPrevention.get()) return;

        if (state.getValue(RespawnAnchorBlock.CHARGE) > 0 && !level.dimensionType().respawnAnchorWorks()) {

            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("spicedcider.respawn_blocked"), true);
            }

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}