package com.evandev.spicedcider.mixin.spelunkery;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.ordana.spelunkery.blocks.entity.BuddingAmethystBlockEntity;
import com.ordana.spelunkery.items.AmethystTuningForkItem;
import com.ordana.spelunkery.reg.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("spelunkery")
@Mixin(BuddingAmethystBlockEntity.class)
public class BuddingAmethystNearestGeodeMixin {

    @WrapOperation(
            method = "handleGameEvent",
            at = @At(value = "INVOKE", target = "Lcom/ordana/spelunkery/items/AmethystTuningForkItem;addAmethystTags(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V")
    )
    private void spicedcider$keepNearestGeode(ResourceKey<Level> dimension, BlockPos pos, ItemStack fork, Operation<Void> original, @Local(argsOnly = true) GameEvent.Context context) {
        Entity source = context.sourceEntity();
        GlobalPos current = AmethystTuningForkItem.getAmethystPos(fork);
        if (source != null && current != null && current.dimension() == dimension
                && pos.distToCenterSqr(source.position()) < current.pos().distToCenterSqr(source.position())) {
            fork.remove(ModComponents.AMETHYST_POS.get());
        }
        original.call(dimension, pos, fork);
    }

    @WrapOperation(
            method = "handleGameEvent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V")
    )
    private void spicedcider$silenceEachBlock(ServerLevel level, Player player, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch, Operation<Void> original) {
    }
}
