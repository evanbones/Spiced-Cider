package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.level.blocks.CopperChestBlock;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("vanillabackport")
@Mixin(CopperChestBlock.class)
public class CopperGolemNoChestMixin {

    @Inject(method = "getFromCopperBlock", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$noChest(Block copperBlock, Direction facing, Level level, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(Blocks.AIR.defaultBlockState());
    }
}
