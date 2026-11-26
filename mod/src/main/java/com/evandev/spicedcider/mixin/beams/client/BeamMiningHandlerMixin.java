package com.evandev.spicedcider.mixin.beams.client;

import com.evandev.spicedcider.compat.beams.BeamMaterials;
import com.evandev.spicedcider.compat.beams.client.BeamCrackRenderer;
import com.evandev.spicedcider.compat.beams.client.BeamParticles;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.syl.beams.client.event.BeamMiningHandler;
import com.syl.beams.targeting.BeamTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("beams")
@Mixin(value = BeamMiningHandler.class, remap = false)
public class BeamMiningHandlerMixin {

    @WrapOperation(method = "onClientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F", remap = true))
    private static float spicedcider$castIronProgress(BlockState state, Player player, BlockGetter level, BlockPos pos, Operation<Float> original, @Local BeamTargeting.Result result) {
        if (BeamMaterials.isCastIron(result.link())) return BeamMaterials.castIronDestroyProgress(player, level, pos);
        return original.call(state, player, level, pos);
    }

    @WrapOperation(method = "onClientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;", remap = true))
    private static SoundType spicedcider$castIronHitSound(BlockState state, Operation<SoundType> original, @Local BeamTargeting.Result result) {
        if (BeamMaterials.isCastIron(result.link())) return SoundType.METAL;
        return original.call(state);
    }

    @WrapOperation(method = "onClientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlockProgress(ILnet/minecraft/core/BlockPos;I)V", remap = true))
    private static void spicedcider$beamCracks(Level level, int breakerId, BlockPos pos, int progress, Operation<Void> original, @Local BeamTargeting.Result result) {
        BeamCrackRenderer.set(result.link(), progress);
        original.call(level, breakerId, pos, progress);
    }

    @Inject(method = "onClientTick", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/network/PacketDistributor;sendToServer(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;[Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V"))
    private static void spicedcider$beamBreakParticles(CallbackInfo ci, @Local BeamTargeting.Result result) {
        BeamParticles.breakBurst(result.link());
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private static void spicedcider$clearCracks(CallbackInfo ci) {
        BeamCrackRenderer.clear();
    }
}
