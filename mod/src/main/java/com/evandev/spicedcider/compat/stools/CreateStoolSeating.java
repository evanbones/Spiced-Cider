package com.evandev.spicedcider.compat.stools;

import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateStoolSeating {
    @Nullable
    static InteractionResult use(BlockState state, Level level, BlockPos pos, Player player) {
        if (player instanceof FakePlayer) return null;
        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
        if (!seats.isEmpty()) {
            if (!level.isClientSide) {
                for (SeatEntity seat : seats) {
                    seat.ejectPassengers();
                    seat.discard();
                }
            }
            return null;
        }
        if (QuarkStools.isPlayerSeated(state)) return null;
        var leashed = SeatBlock.getLeashed(level, player);
        if (!leashed.isPresent()) return null;
        if (!level.isClientSide) {
            sitDown(level, pos, state, leashed.get());
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    static boolean landOn(Entity entity) {
        if (entity instanceof Player || !(entity instanceof LivingEntity) || !SeatBlock.canBePickedUp(entity)) return false;
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockState state = level.getBlockState(pos);
        if (!QuarkStools.isStool(state) || QuarkStools.isPlayerSeated(state)) return false;
        if (!level.getEntitiesOfClass(SeatEntity.class, new AABB(pos)).isEmpty()) return false;
        if (!level.isClientSide) {
            sitDown(level, pos, state, entity);
        }
        return true;
    }

    private static void sitDown(Level level, BlockPos pos, BlockState state, Entity entity) {
        SeatEntity seat = new SeatEntity(level);
        seat.setPos(pos.getX() + 0.5, pos.getY() + (QuarkStools.isBig(state) ? 0.5 : 1.0 / 16.0), pos.getZ() + 0.5);
        level.addFreshEntity(seat);
        entity.startRiding(seat, true);
        if (entity instanceof TamableAnimal tamable) {
            tamable.setInSittingPose(true);
        }
    }
}
