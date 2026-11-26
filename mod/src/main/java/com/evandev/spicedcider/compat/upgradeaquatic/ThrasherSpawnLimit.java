package com.evandev.spicedcider.compat.upgradeaquatic;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.upgradeaquatic.ThrasherAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ThrasherSpawnLimit {
    private static final double RADIUS = 64.0;

    @SubscribeEvent
    public static void onPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (!(event.getEntity() instanceof ThrasherAccessor)) return;
        MobSpawnType type = event.getSpawnType();
        if (type != MobSpawnType.NATURAL && type != MobSpawnType.CHUNK_GENERATION) return;
        int limit = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.thrasherSpawnLimit, 0);
        if (limit <= 0) return;
        Mob mob = event.getEntity();
        AABB area = AABB.ofSize(mob.position(), RADIUS * 2.0, RADIUS * 2.0, RADIUS * 2.0);
        int nearby = event.getLevel().getEntitiesOfClass(Mob.class, area,
                other -> other instanceof ThrasherAccessor && other.isAlive() && other.distanceToSqr(mob) <= RADIUS * RADIUS).size();
        if (nearby >= limit) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }
}
