package com.evandev.spicedcider.mixin.upgradeaquatic;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@IfModLoaded("upgrade_aquatic")
@Mixin(targets = "com.teamabnormals.upgrade_aquatic.common.entity.monster.Thrasher", remap = false)
public interface ThrasherAccessor {

    @Invoker("setStunned")
    void spicedcider$setStunned(int ticks);
}
