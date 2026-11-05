package com.evandev.spicedcider.compat.tinytakeover;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.tiny_takeover_backport.entity.AgeLockable;
import com.evandev.tiny_takeover_backport.registry.ModRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class GoldenDandelions {
    public static boolean tempts(Mob mob, LivingEntity target) {
        if (!mob.isBaby() || !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.goldenDandelionTempting, false)) return false;
        if (mob instanceof AgeLockable lockable && lockable.tiny_takeover_backport$isAgeLocked()) return false;
        return target.getMainHandItem().is(ModRegistry.GOLDEN_DANDELION_ITEM) || target.getOffhandItem().is(ModRegistry.GOLDEN_DANDELION_ITEM);
    }
}
