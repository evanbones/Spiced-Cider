package com.evandev.spicedcider.mixin.blockbox;

import com.evandev.spicedcider.compat.blockbox.BlockBoxSeatMobs;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.blockbox.common.block.SeatBlock;

@IfModLoaded("blockbox")
@Mixin(SeatBlock.class)
public class SeatBlockMobsMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void spicedcider$seatLeashedMob(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!BlockBoxSeatMobs.enabled() || player.isShiftKeyDown()) return;
        if (BlockBoxSeatMobs.ejectMob(level, pos)) {
            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            return;
        }
        BlockBoxSeatMobs.leashedTo(level, player).ifPresent(mob -> {
            if (BlockBoxSeatMobs.seat(level, pos, mob)) cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
        });
    }

    public void updateEntityAfterFallOn(BlockGetter getter, Entity entity) {
        if (BlockBoxSeatMobs.enabled() && getter instanceof Level level && BlockBoxSeatMobs.canSit(entity)
                && BlockBoxSeatMobs.seat(level, entity.blockPosition(), entity)) {
            return;
        }
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
    }
}
