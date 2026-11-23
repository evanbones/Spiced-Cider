package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;

import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ThreatGlowEvents {
    private static final int GLOW_TICKS = 60;

    public static boolean enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.revealThreateningMonsters, false);
    }

    @SubscribeEvent
    public static void onSleep(CanPlayerSleepEvent event) {
        if (event.getProblem() != Player.BedSleepingProblem.NOT_SAFE || !enabled()) return;
        ServerPlayer player = event.getEntity();
        Vec3 bed = Vec3.atBottomCenterOf(event.getPos());
        AABB box = new AABB(bed.x - 8.0, bed.y - 5.0, bed.z - 8.0, bed.x + 8.0, bed.y + 5.0, bed.z + 8.0);
        glow(player.level().getEntitiesOfClass(Monster.class, box, monster -> monster.isPreventingPlayerRest(player)));
    }

    private static void glow(List<? extends Mob> mobs) {
        for (Mob mob : mobs) {
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_TICKS));
        }
    }
}
