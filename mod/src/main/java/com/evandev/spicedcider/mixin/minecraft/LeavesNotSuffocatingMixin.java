package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LeavesBlock.class)
public class LeavesNotSuffocatingMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private static BlockBehaviour.Properties spicedcider$neverSuffocate(BlockBehaviour.Properties properties) {
        return properties.isSuffocating(LeavesNotSuffocatingMixin::spicedcider$suffocates).isViewBlocking(LeavesNotSuffocatingMixin::spicedcider$suffocates);
    }

    @Unique
    private static boolean spicedcider$suffocates(BlockState state, BlockGetter level, BlockPos pos) {
        return !SpicedCiderConfig.startupOr(SpicedCiderConfig.STARTUP.leavesNotSuffocating, true) && state.blocksMotion() && state.isCollisionShapeFullBlock(level, pos);
    }
}
