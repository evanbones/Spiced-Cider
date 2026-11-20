package com.evandev.spicedcider.mixin.quark;

import com.evandev.spicedcider.compat.stools.StoolSeats;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.content.building.block.StoolBlock;

@IfModLoaded("quark")
@Mixin(StoolBlock.class)
public class StoolBlockSeatMixin {
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void spicedcider$seatLeashedMob(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = StoolSeats.use(state, level, pos, player);
        if (result != null) cir.setReturnValue(result);
    }

    @Inject(method = "updateEntityAfterFallOn", at = @At("HEAD"), cancellable = true)
    private void spicedcider$seatLandingMob(BlockGetter level, Entity entity, CallbackInfo ci) {
        if (StoolSeats.landOn(entity)) ci.cancel();
    }

    @Nullable
    public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return StoolSeats.enabled() ? PathType.RAIL : null;
    }
}
