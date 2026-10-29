package com.evandev.spicedcider.mixin.emi;

import com.evandev.spicedcider.mixin.controlify.KeyboardOverlayScreenAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.screen.EmiScreenBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("emi")
@IfModLoaded("controlify")
@Mixin(value = {EmiScreenBase.class, EmiApi.class}, remap = false)
public class EmiKeyboardOverlayScreenMixin {

    @WrapOperation(method = {"getCurrent", "getHandledScreen"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;"), require = 1)
    private static Screen spicedcider$screenUnderKeyboard(Minecraft minecraft, Operation<Screen> original) {
        Screen screen = original.call(minecraft);
        return screen instanceof KeyboardOverlayScreenAccessor overlay ? overlay.spicedcider$getBackgroundScreen() : screen;
    }
}
