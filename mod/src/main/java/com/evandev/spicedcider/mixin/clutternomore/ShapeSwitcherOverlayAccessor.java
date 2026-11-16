package com.evandev.spicedcider.mixin.clutternomore;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("clutternomore")
@Mixin(value = ShapeSwitcherOverlay.class, remap = false)
public interface ShapeSwitcherOverlayAccessor {

    @Accessor("hand")
    InteractionHand spicedcider$getHand();
}
