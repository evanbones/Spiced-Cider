package com.evandev.spicedcider.mixin.minecraft.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.InBedChatScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatComponent.class)
public abstract class ChatComponentInBedMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyReturnValue(method = "isChatFocused", at = @At("RETURN"))
    private boolean spicedcider$unfocusInBed(boolean focused) {
        return focused && !(this.minecraft.screen instanceof InBedChatScreen);
    }
}
