package com.evandev.spicedcider.mixin.connectiblechains;

import com.evandev.connectiblechains.entity.Chainable;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("connectiblechains")
@Mixin(targets = "com.evandev.connectiblechains.util.ChainCollisionIndex", remap = false)
public class ChainCollisionLeadMixin {

    @Inject(method = "ensure", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$noLeadCollision(Level level, Entity owner, Entity holder, Chainable.ChainData chainData, CallbackInfo ci) {
        if (chainData.sourceItem == Items.LEAD) ci.cancel();
    }
}
