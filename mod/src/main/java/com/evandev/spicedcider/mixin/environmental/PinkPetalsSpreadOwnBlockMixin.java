package com.evandev.spicedcider.mixin.environmental;

import com.bawnorton.mixinsquared.TargetHandler;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("environmental")
@Mixin(value = PinkPetalsBlock.class, priority = 1500)
public class PinkPetalsSpreadOwnBlockMixin {

    @TargetHandler(mixin = "com.teamabnormals.environmental.core.mixin.PinkPetalsBlockMixin", name = "performBonemeal")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void spicedcider$spreadOwnFlowerBed(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo outer, CallbackInfo ci) {
        if (state.is(Blocks.PINK_PETALS)) return;
        ci.cancel();
        if (state.getValue(PinkPetalsBlock.AMOUNT) < 4) return;

        Block block = state.getBlock();
        List<BlockPos> spots = BlockPos.betweenClosedStream(pos.offset(-2, -1, -2), pos.offset(2, 1, 2))
                .filter(target -> Mth.abs(pos.getX() - target.getX()) != 2 || Mth.abs(pos.getZ() - target.getZ()) != 2)
                .filter(target -> state.canSurvive(level, target) && (level.isEmptyBlock(target) || spicedcider$canGrow(level.getBlockState(target), block)))
                .map(BlockPos::immutable)
                .toList();
        if (spots.isEmpty()) return;

        BlockPos target = spots.get(random.nextInt(spots.size()));
        BlockState current = level.getBlockState(target);
        if (spicedcider$canGrow(current, block)) {
            level.setBlockAndUpdate(target, current.setValue(PinkPetalsBlock.AMOUNT, current.getValue(PinkPetalsBlock.AMOUNT) + 1));
        } else {
            level.setBlockAndUpdate(target, block.defaultBlockState().setValue(PinkPetalsBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)));
        }
        outer.cancel();
    }

    @Unique
    private static boolean spicedcider$canGrow(BlockState state, Block block) {
        return state.is(block) && state.getValue(PinkPetalsBlock.AMOUNT) < 4;
    }
}
