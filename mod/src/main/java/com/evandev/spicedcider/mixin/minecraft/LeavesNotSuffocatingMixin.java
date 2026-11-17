package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LeavesBlock.class)
public class LeavesNotSuffocatingMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
    private static BlockBehaviour.Properties spicedcider$neverSuffocate(BlockBehaviour.Properties properties) {
        BlockPropertiesAccessor accessor = (BlockPropertiesAccessor) properties;
        BlockBehaviour.StatePredicate suffocating = accessor.spicedcider$getIsSuffocating();
        BlockBehaviour.StatePredicate viewBlocking = accessor.spicedcider$getIsViewBlocking();
        return properties
                .isSuffocating((state, level, pos) -> !spicedcider$enabled() && suffocating.test(state, level, pos))
                .isViewBlocking((state, level, pos) -> !spicedcider$enabled() && viewBlocking.test(state, level, pos));
    }

    @Unique
    private static boolean spicedcider$enabled() {
        return SpicedCiderConfig.startupOr(SpicedCiderConfig.STARTUP.leavesNotSuffocating, true);
    }
}
