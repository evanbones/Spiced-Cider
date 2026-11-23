package com.evandev.spicedcider.mixin.breakmyfall;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("break_my_fall")
@Mixin(targets = "com.kekecreations.break_my_fall.event.KekeFallEvent", remap = false)
public class FallBreakIceMeltMixin {

    @WrapOperation(method = "onFall", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private static boolean spicedcider$meltIce(Level level, BlockPos pos, boolean drop, Entity entity, Operation<Boolean> original) {
        BlockState state = level.getBlockState(pos);
        boolean destroyed = original.call(level, pos, drop, entity);
        if (!destroyed || !(state.getBlock() instanceof IceBlock) || level.dimensionType().ultraWarm()) return destroyed;
        BlockState below = level.getBlockState(pos.below());
        if (below.blocksMotion() || below.liquid()) {
            level.setBlockAndUpdate(pos, IceBlock.meltsInto());
        }
        return destroyed;
    }
}
