package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.compat.redomesticate.RedomesticatePets;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.neoforged.fml.ModList;

public class FollowingPets {

    private static final boolean REDOMESTICATE = ModList.get().isLoaded("redomesticate");

    public static boolean isFollowingOwner(Entity entity) {
        if (entity instanceof TamableAnimal pet) {
            if (!pet.isTame() || pet.unableToMoveToOwner()) return false;
            LivingEntity owner = pet.getOwner();
            return owner != null && owner.level() == pet.level();
        }
        return REDOMESTICATE && RedomesticatePets.isFollowingOwner(entity);
    }
}
