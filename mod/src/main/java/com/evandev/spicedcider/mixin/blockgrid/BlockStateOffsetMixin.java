package com.evandev.spicedcider.mixin.blockgrid;

import com.evandev.spicedcider.blockgrid.ServerOffsetSync;
import com.evandev.spicedcider.blockgrid.SupportOffsets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateOffsetMixin {
    @ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
    private boolean spicedcider$surviveOnPartialSurface(boolean original, LevelReader level, BlockPos pos) {
        BlockState self = spicedcider$self();
        if (SupportOffsets.isSitter(self)) {
            Direction anchor = SupportOffsets.anchorOf(self);
            if (anchor != null) {
                BlockState supporting = level.getBlockState(pos.relative(anchor));
                if (SupportOffsets.isSitter(supporting) || SupportOffsets.isFacingMounted(supporting)) {
                    return false;
                }
            }
        }
        return original || SupportOffsets.hasPartialSupport(self, level, pos);
    }

    @ModifyReturnValue(method = "hasLargeCollisionShape", at = @At("RETURN"))
    private boolean spicedcider$extendCollisionLookup(boolean original) {
        return original || SupportOffsets.mayOffset(spicedcider$self());
    }

    @ModifyReturnValue(method = "isRedstoneConductor", at = @At("RETURN"))
    private boolean spicedcider$conductThroughOffsetNeighbor(boolean original, BlockGetter level, BlockPos pos) {
        return original || SupportOffsets.carriesAttachedSignal(spicedcider$self(), level, pos);
    }

    @ModifyReturnValue(method = "getOffset", at = @At("RETURN"))
    private Vec3 spicedcider$shiftRenderToSupport(Vec3 original, BlockGetter level, BlockPos pos) {
        Vec3 shift = SupportOffsets.offsetFor(spicedcider$self(), level, pos);
        return shift == Vec3.ZERO ? original : original.add(shift);
    }

    @ModifyReturnValue(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
    private VoxelShape spicedcider$shiftShapeToSupport(VoxelShape original, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 shift = SupportOffsets.offsetFor(spicedcider$self(), level, pos);
        return shift == Vec3.ZERO ? original : original.move(shift.x, shift.y, shift.z);
    }

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void spicedcider$dropStoredOffset(Level level, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel && !spicedcider$self().is(newState.getBlock())) {
            ServerOffsetSync.onBlockRemoved(serverLevel, pos);
        }
    }

    @Unique
    private BlockState spicedcider$self() {
        return (BlockState) (Object) this;
    }
}
