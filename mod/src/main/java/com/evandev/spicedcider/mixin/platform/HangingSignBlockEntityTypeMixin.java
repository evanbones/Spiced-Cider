package com.evandev.spicedcider.mixin.platform;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("platform")
@Mixin(BlockEntityType.class)
public class HangingSignBlockEntityTypeMixin {

    @WrapMethod(method = "isValid")
    private boolean spicedcider$keepHangingSignsOffSign(BlockState state, Operation<Boolean> original) {
        if ((Object) this == BlockEntityType.SIGN) {
            Block block = state.getBlock();
            if (block instanceof CeilingHangingSignBlock || block instanceof WallHangingSignBlock) {
                return false;
            }
        }
        return original.call(state);
    }
}
