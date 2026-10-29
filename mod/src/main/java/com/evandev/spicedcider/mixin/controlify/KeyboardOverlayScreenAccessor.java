package com.evandev.spicedcider.mixin.controlify;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.screenop.keyboard.KeyboardOverlayScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("controlify")
@Mixin(value = KeyboardOverlayScreen.class, remap = false)
public interface KeyboardOverlayScreenAccessor {

    @Accessor("backgroundScreen")
    Screen spicedcider$getBackgroundScreen();
}
