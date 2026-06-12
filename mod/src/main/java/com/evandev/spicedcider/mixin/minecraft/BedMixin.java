package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BedBlock.class)
public abstract class BedMixin extends Block {

    public BedMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void spicedcider$onUseWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!level.dimensionType().bedWorks()) {
            if (!level.isClientSide) {
                cir.setReturnValue(InteractionResult.SUCCESS);

                if (state.getValue(BedBlock.PART) != BedPart.HEAD) {
                    pos = pos.relative(state.getValue(BedBlock.FACING));
                    state = level.getBlockState(pos);

                    if (!state.is(this)) {
                        cir.setReturnValue(InteractionResult.CONSUME);
                        return;
                    }

                    if (level.dimension() != Level.NETHER && level.dimension() != Level.END) {
                        level.removeBlock(pos, false);
                        BlockPos footPos = pos.relative(state.getValue(BedBlock.FACING).getOpposite());
                        if (level.getBlockState(footPos).is(this)) {
                            level.removeBlock(footPos, false);
                        }
                        return;
                    }

                    player.displayClientMessage(Component.translatable("sleep.not_possible"), true);
                } else {
                    if (state.getValue(BedBlock.OCCUPIED)) {
                        if (!this.spicedcider$wakeVillager(level, pos)) {
                            player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("sleep.not_possible"), true);
                    }
                }
            } else {
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Unique
    private boolean spicedcider$wakeVillager(Level level, BlockPos pos) {
        List<Villager> list = level.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        } else {
            list.getFirst().stopSleeping();
            return true;
        }
    }
}