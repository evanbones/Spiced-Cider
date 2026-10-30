package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LeavesBlock.class)
public class LeavesNotSuffocatingMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private static BlockBehaviour.Properties spicedcider$neverSuffocate(BlockBehaviour.Properties properties) {
        return properties.isSuffocating((state, level, pos) -> false).isViewBlocking((state, level, pos) -> false);
    }
}
