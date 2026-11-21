package com.evandev.spicedcider.mixin.offgrid;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Surfaces;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("offgrid")
@Mixin(value = Surfaces.class, remap = false)
public class SurfacesStairSignMixin {

    @Inject(method = "offsetFor", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$keepSignsOnStairs(Level level, BlockPos pos, Direction face, Vec3 click, BlockState state, CallbackInfoReturnable<Vec3> cir) {
        if (state.is(BlockTags.WALL_SIGNS) && level.getBlockState(pos).getBlock() instanceof StairBlock) {
            cir.setReturnValue(Vec3.ZERO);
        }
    }
}
