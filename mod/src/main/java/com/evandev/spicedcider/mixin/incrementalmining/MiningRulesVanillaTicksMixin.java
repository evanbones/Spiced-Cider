package com.evandev.spicedcider.mixin.incrementalmining;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("incrementalmining")
@Mixin(targets = "com.jdynamo.incrementalmining.events.MiningRules", remap = false)
public class MiningRulesVanillaTicksMixin {

    @Inject(method = "getIncrementalMiningTicks", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$vanillaTicks(BlockState state, ServerPlayer player, BlockPos pos, ItemStack tool, CallbackInfoReturnable<Integer> cir) {
        if (state.getDestroySpeed(player.level(), pos) < 0.0F) {
            cir.setReturnValue(-1);
            return;
        }
        float progress = state.getDestroyProgress(player, player.level(), pos);
        cir.setReturnValue(progress <= 0.0F ? Integer.MAX_VALUE / 2 : Math.max(1, Mth.ceil(1.0F / progress)));
    }
}
