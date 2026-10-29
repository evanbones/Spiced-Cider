package com.evandev.spicedcider.mixin.controlify;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.gui.screen.RadialMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("controlify")
@Mixin(value = RadialMenuScreen.class, remap = false)
public interface RadialMenuScreenAccessor {

    @Accessor("radialRadius")
    float spicedcider$radialRadius();
}
