package com.evandev.spicedcider.mixin.copperagebackport;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("copperagebackport")
@Mixin(targets = "com.github.smallinger.copperagebackport.event.CopperGolemSpawnLogic", remap = false)
public class CopperGolemSummonTriggerMixin {

    @WrapOperation(
            method = "trySpawnCopperGolem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", remap = true)
    )
    private static boolean spicedcider$noChest(ServerLevel level, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
        if (state.getBlock() instanceof ChestBlock) {
            return original.call(level, pos, Blocks.AIR.defaultBlockState(), flags);
        }
        return original.call(level, pos, state, flags);
    }

    @WrapOperation(
            method = "trySpawnCopperGolem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", remap = true)
    )
    private static boolean spicedcider$triggerSummoned(ServerLevel level, Entity golem, Operation<Boolean> original) {
        boolean added = original.call(level, golem);
        if (added) {
            for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate(5.0))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(player, golem);
            }
        }
        return added;
    }
}
