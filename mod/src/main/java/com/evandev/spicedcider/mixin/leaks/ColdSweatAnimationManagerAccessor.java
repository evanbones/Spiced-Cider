package com.evandev.spicedcider.mixin.leaks;

import com.momosoftworks.coldsweat.client.renderer.animation.AnimationManager;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@IfModLoaded("cold_sweat")
@Mixin(value = AnimationManager.class, remap = false)
public interface ColdSweatAnimationManagerAccessor {
    @Accessor("ANIMATION_STATES")
    static Map<Entity, Map<String, PartPose>> spicedcider$animationStates() {
        throw new AssertionError();
    }
}
