package com.evandev.spicedcider.compat.redomesticate;

import com.evandev.redomesticate.api.ICommandableMob;
import com.evandev.redomesticate.api.ITameableEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class RedomesticatePets {

    public static boolean isFollowingOwner(Entity entity) {
        if (!(entity instanceof Mob mob) || !(mob instanceof ITameableEntity tameable) || !(mob instanceof ICommandableMob commandable)) return false;
        if (!tameable.redomesticate$isTame() || !commandable.redomesticate$isFollowingOwner()) return false;
        if (mob.isLeashed() || mob.isPassenger()) return false;
        LivingEntity owner = tameable.redomesticate$getTameOwner();
        return owner != null && !owner.isSpectator();
    }
}
