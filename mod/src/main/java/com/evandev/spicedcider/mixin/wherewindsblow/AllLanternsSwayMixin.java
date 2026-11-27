package com.evandev.spicedcider.mixin.wherewindsblow;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("where_winds_blow")
@Mixin(targets = {
        "com.jvn.wherewindsblow.client.lantern.LanternSwayModel",
        "com.jvn.wherewindsblow.client.lantern.SwingingLanternAssemblyRenderer"
}, remap = false)
public class AllLanternsSwayMixin {

    @WrapOperation(method = {"isHangingLantern", "hasHangingLanternBelow"}, require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean spicedcider$anyLantern(BlockState state, Block block, Operation<Boolean> original) {
        return original.call(state, block) || block == Blocks.LANTERN && state.getBlock() instanceof LanternBlock;
    }
}
