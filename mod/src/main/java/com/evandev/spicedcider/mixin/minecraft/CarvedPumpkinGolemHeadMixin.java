package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.entity.PaleSnowGolem;
import com.evandev.spicedcider.registry.ModTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinGolemHeadMixin {
    @ModifyArg(method = {"getOrCreateSnowGolemFull", "getOrCreateIronGolemFull"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/pattern/BlockInWorld;hasState(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;", ordinal = 0))
    private Predicate<net.minecraft.world.level.block.state.BlockState> spicedcider$tagHeads(Predicate<net.minecraft.world.level.block.state.BlockState> original) {
        return state -> original.test(state) || state != null && state.is(ModTags.Blocks.GOLEM_HEADS);
    }

    @WrapOperation(method = "trySpawnGolem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;spawnGolemInWorld(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)V"))
    private void spicedcider$paleHead(Level level, BlockPattern.BlockPatternMatch match, Entity golem, BlockPos pos, Operation<Void> original) {
        if (golem instanceof SnowGolem snowGolem) {
            BlockInWorld head = match.getBlock(0, 0, 0);
            if (head.getState().is(ModTags.Blocks.PALE_GOLEM_HEADS)) {
                ((PaleSnowGolem) snowGolem).spicedcider$setPalePumpkin(true);
            }
        }
        original.call(level, match, golem, pos);
    }
}
