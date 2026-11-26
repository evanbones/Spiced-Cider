package com.evandev.spicedcider.mixin.beams;

import com.evandev.spicedcider.compat.beams.BeamCollisionIndex;
import com.evandev.spicedcider.compat.beams.BeamRaytrace;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.syl.beams.attachment.BeamPlacementData;
import com.syl.beams.items.BeamItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("beams")
@Mixin(value = BeamItem.class, remap = false)
public class BeamItemMixin {

    @ModifyArg(method = "raytraceSkippingAnchors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", remap = true))
    private static float spicedcider$frameEyePosition(float partialTick, @Local(argsOnly = true) Level level) {
        return BeamRaytrace.partialTick(level);
    }

    @ModifyArg(method = "raytraceSkippingAnchors", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getViewVector(F)Lnet/minecraft/world/phys/Vec3;", remap = true))
    private static float spicedcider$frameViewVector(float partialTick, @Local(argsOnly = true) Level level) {
        return BeamRaytrace.partialTick(level);
    }

    @Inject(method = "placeEnd", at = @At("HEAD"), cancellable = true)
    private void spicedcider$rejectThroughBlocks(ServerPlayer player, Level level, UseOnContext ctx, BeamPlacementData start, BeamPlacementData clicked, CallbackInfoReturnable<InteractionResult> cir) {
        if (BeamCollisionIndex.obstructedBetween(level, start.toWorldVec3(), clicked.toWorldVec3())) {
            player.displayClientMessage(Component.translatable("message.spicedcider.beam_obstructed"), true);
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
