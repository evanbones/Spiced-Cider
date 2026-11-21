package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {

    @Accessor("inGround")
    boolean spicedcider$inGround();
}
