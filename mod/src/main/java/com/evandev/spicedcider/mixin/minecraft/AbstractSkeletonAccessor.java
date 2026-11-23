package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSkeleton.class)
public interface AbstractSkeletonAccessor {

    @Invoker("getAttackInterval")
    int spicedcider$getAttackInterval();

    @Invoker("getHardAttackInterval")
    int spicedcider$getHardAttackInterval();
}
