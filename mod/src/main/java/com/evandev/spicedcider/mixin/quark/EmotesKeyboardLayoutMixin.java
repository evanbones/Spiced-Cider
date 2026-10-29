package com.evandev.spicedcider.mixin.quark;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.screenop.keyboard.ChatKeyboardDucky;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("quark")
@IfModLoaded("controlify")
@Mixin(targets = "org.violetmoon.quark.content.tweaks.module.EmotesModule$Client", remap = false)
public class EmotesKeyboardLayoutMixin {

    @WrapOperation(method = "initGui", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScaledHeight()I"))
    private int spicedcider$aboveKeyboard(Window window, Operation<Integer> original) {
        int height = original.call(window);
        if (!(Minecraft.getInstance().screen instanceof ChatScreen chat)) return height;
        return height - (int) (height * ChatKeyboardDucky.getKeyboardShiftAmount(chat));
    }
}
