package com.evandev.spicedcider.compat.blockbox;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import vectorwing.blockbox.common.block.SeatBlock;
import vectorwing.blockbox.common.entity.SeatEntity;

import java.util.List;
import java.util.Optional;

public class BlockBoxSeatMobs {
    private static final double LEASH_RANGE = 10.0;
    private static final int RESEAT_COOLDOWN_TICKS = 60;
    private static final java.util.Map<Entity, Long> STOOD_UP = new java.util.WeakHashMap<>();

    public static boolean enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.blockBoxSeatMobs, false);
    }

    public static boolean canSit(Entity entity) {
        if (!(entity instanceof LivingEntity) || entity instanceof Player || entity instanceof Shulker
                || entity.isPassenger() || entity.isVehicle() || !entity.getType().getCategory().isFriendly()) return false;
        Long stoodUp = STOOD_UP.get(entity);
        return stoodUp == null || entity.level().getGameTime() - stoodUp > RESEAT_COOLDOWN_TICKS;
    }

    public static List<SeatEntity> seatsAt(Level level, BlockPos pos) {
        return level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
    }

    public static boolean ejectMob(Level level, BlockPos pos) {
        List<SeatEntity> seats = seatsAt(level, pos);
        boolean ejected = false;
        for (SeatEntity seat : seats) {
            if (seat.getPassengers().stream().anyMatch(passenger -> !(passenger instanceof Player))) {
                if (!level.isClientSide) {
                    seat.getPassengers().forEach(passenger -> STOOD_UP.put(passenger, level.getGameTime()));
                    seat.ejectPassengers();
                    seat.discard();
                }
                ejected = true;
            }
        }
        return ejected;
    }

    public static Optional<Mob> leashedTo(Level level, Player player) {
        return level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(LEASH_RANGE),
                mob -> mob.getLeashHolder() == player && canSit(mob)).stream().findFirst();
    }

    public static boolean seat(Level level, BlockPos pos, Entity entity) {
        if (!seatsAt(level, pos).isEmpty()) return false;
        if (!level.isClientSide) {
            if (entity instanceof Mob mob && mob.isLeashed()) mob.dropLeash(true, true);
            SeatBlock.sitDown(level, pos, entity);
        }
        return true;
    }
}
