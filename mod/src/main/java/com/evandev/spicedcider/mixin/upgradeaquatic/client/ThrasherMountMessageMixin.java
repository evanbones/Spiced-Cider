package com.evandev.spicedcider.mixin.upgradeaquatic.client;

import com.evandev.spicedcider.mixin.upgradeaquatic.ThrasherAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("upgrade_aquatic")
@Mixin(ClientPacketListener.class)
public class ThrasherMountMessageMixin {

    @WrapOperation(method = "handleSetEntityPassengersPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void spicedcider$noThrasherDismountHint(Gui gui, Component message, boolean animate, Operation<Void> original, @Local(ordinal = 0) Entity vehicle) {
        if (!(vehicle instanceof ThrasherAccessor)) original.call(gui, message, animate);
    }

    @WrapOperation(method = "handleSetEntityPassengersPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GameNarrator;sayNow(Lnet/minecraft/network/chat/Component;)V"))
    private void spicedcider$noThrasherDismountNarration(GameNarrator narrator, Component message, Operation<Void> original, @Local(ordinal = 0) Entity vehicle) {
        if (!(vehicle instanceof ThrasherAccessor)) original.call(narrator, message);
    }
}
