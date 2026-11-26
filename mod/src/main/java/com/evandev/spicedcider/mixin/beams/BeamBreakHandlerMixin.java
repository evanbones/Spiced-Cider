package com.evandev.spicedcider.mixin.beams;

import com.evandev.spicedcider.compat.beams.BeamMaterials;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.syl.beams.blockentities.BeamBlockEntity;
import com.syl.beams.event.BeamBreakHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("beams")
@Mixin(value = BeamBreakHandler.class, remap = false)
public class BeamBreakHandlerMixin {

    @WrapOperation(method = "breakOneLinkAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", remap = true))
    private static void spicedcider$castIronBreakSound(Level level, Player player, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch, Operation<Void> original, @Local BeamBlockEntity.BeamLink target) {
        original.call(level, player, pos, BeamMaterials.isCastIron(target) ? SoundEvents.METAL_BREAK : sound, source, volume, pitch);
    }

    @WrapOperation(method = "breakOneLinkAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", remap = true))
    private static void spicedcider$castIronNeedsPickaxe(Level level, BlockPos pos, ItemStack stack, Operation<Void> original, @Local(argsOnly = true) ServerPlayer player, @Local BeamBlockEntity.BeamLink target) {
        if (BeamMaterials.isCastIron(target) && !BeamMaterials.canHarvestCastIron(player)) return;
        original.call(level, pos, stack);
    }
}
