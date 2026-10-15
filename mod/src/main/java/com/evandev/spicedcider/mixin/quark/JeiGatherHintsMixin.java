package com.evandev.spicedcider.mixin.quark;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("zeta")
@Mixin(targets = "org.violetmoon.zeta.integration.JeiGatherHints", remap = false)
public class JeiGatherHintsMixin {

    @WrapMethod(method = "accept(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/network/chat/Component;)V")
    private void spicedcider$dropHint(ItemLike item, Component text, Operation<Void> original) {
    }
}
