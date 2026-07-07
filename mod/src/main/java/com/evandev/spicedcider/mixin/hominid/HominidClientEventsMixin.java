package com.evandev.spicedcider.mixin.hominid;

import com.alganaut.hominid.registry.entity.HominidEntityCreator;
import com.alganaut.hominid.registry.entity.custom.Bellman;
import com.alganaut.hominid.registry.entity.custom.Juggernaut;
import com.alganaut.hominid.registry.entity.custom.Vampire;
import com.alganaut.hominid.registry.event.HominidClientEvents;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


// hominid man I love you but you gotta leave my console alone
@Mixin(value = HominidClientEvents.class, remap = false)
public class HominidClientEventsMixin {

    @Inject(method = "onEntityJoinWorld", at = @At("HEAD"), cancellable = true)
    private static void fixZombieReplacement(FinalizeSpawnEvent event, CallbackInfo ci) {

        if (event.getEntity() instanceof AbstractIllager illager) {
            illager.targetSelector.addGoal(3, new AvoidEntityGoal<>(illager, Vampire.class, 6.0F, 1.0D, 1.2D));
        }

        if (event.getEntity().getClass() == Zombie.class) {
            Zombie zombie = (Zombie) event.getEntity();
            double rand = Math.random();

            if (rand < 0.1) {
                event.setCanceled(true);

                Juggernaut customMob = new Juggernaut(HominidEntityCreator.JUGGERNAUT.get(), zombie.level());
                customMob.setPos(zombie.position().x, zombie.position().y, zombie.position().z);
                zombie.level().addFreshEntity(customMob);
            } else if (rand < 0.2) {
                event.setCanceled(true);

                Bellman customMob = new Bellman(HominidEntityCreator.BELLMAN.get(), zombie.level());
                customMob.setPos(zombie.position().x, zombie.position().y, zombie.position().z);
                zombie.level().addFreshEntity(customMob);
            }
        }

        ci.cancel();
    }
}