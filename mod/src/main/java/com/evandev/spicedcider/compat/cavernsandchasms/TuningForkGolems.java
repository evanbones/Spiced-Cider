package com.evandev.spicedcider.compat.cavernsandchasms;

import com.teamabnormals.caverns_and_chasms.common.item.copper.TuningForkItem;
import com.teamabnormals.caverns_and_chasms.core.interfaces.ControllableGolem;
import com.teamabnormals.caverns_and_chasms.core.other.CCCriteriaTriggers;
import com.teamabnormals.caverns_and_chasms.core.other.CCGameEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

public final class TuningForkGolems {

    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded("caverns_and_chasms");
        }
        return loaded;
    }

    public static void vibrate(Level level, Entity source, Vec3 pos) {
        level.gameEvent(CCGameEvents.TUNING_FORK_VIBRATE, pos, GameEvent.Context.of(source));
    }

    public static void triggerUse(ServerPlayer player) {
        CCCriteriaTriggers.USE_TUNING_FORK.get().trigger(player);
    }

    public static void attract(BlockPos pos, Player player) {
        TuningForkItem.attractGolemToPos(pos, player);
    }

    public static void orderAttack(LivingEntity target, Player player) {
        TuningForkItem.orderGolemToAttackEntity(target, player);
    }

    public static boolean isControllable(Entity entity, Player player) {
        return entity instanceof ControllableGolem golem && golem.canBeTuningForkControlled(player);
    }

    public static boolean hasControlledGolem(Player player) {
        return TuningForkItem.findControlledGolem(player) != null;
    }

    public static boolean toggleControl(Player player, LivingEntity target) {
        ControllableGolem golem = (ControllableGolem) target;
        Player original = golem.getTuningForkController();
        if (original == player) {
            TuningForkItem.setControlledGolem(player, null);
            golem.onTuningForkControlEnd(player);
            return false;
        }
        TuningForkItem.setControlledGolem(player, golem);
        TuningForkItem.setForgetGolemTime(player, 200);
        if (original != null) {
            TuningForkItem.setControlledGolem(original, null);
        }
        golem.setTuningForkPos(null);
        golem.setTuningForkTarget(null);
        ((Mob) golem).getNavigation().stop();
        ((Mob) golem).setTarget(null);
        golem.onTuningForkControlStart(player);
        return true;
    }
}
