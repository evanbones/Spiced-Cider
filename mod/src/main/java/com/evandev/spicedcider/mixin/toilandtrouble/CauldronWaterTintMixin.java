package com.evandev.spicedcider.mixin.toilandtrouble;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import cc.cassian.cauldrons.core.CauldronContents;
import com.evandev.spicedcider.content.brewing.CauldronBrewing;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("toil_and_trouble")
@Mixin(targets = "cc.cassian.cauldrons.client.CauldronModClient", remap = false)
public class CauldronWaterTintMixin {

    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$biomeWater(BlockState state, @Nullable BlockAndTintGetter view, @Nullable BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if (view == null || pos == null || tintIndex != 0 || !(view.getBlockEntity(pos) instanceof CauldronBlockEntity cauldron)) {
            return;
        }
        CauldronContents contents = cauldron.getContents();
        if (contents.customColor().isEmpty() && contents.customEffects().isEmpty() && CauldronBrewing.isWater(contents.potion())) {
            cir.setReturnValue(BiomeColors.getAverageWaterColor(view, pos));
        }
    }
}
