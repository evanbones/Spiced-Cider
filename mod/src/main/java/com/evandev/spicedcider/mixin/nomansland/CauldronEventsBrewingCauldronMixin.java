package com.evandev.spicedcider.mixin.nomansland;

import com.farcr.nomansland.common.event.CauldronEvents;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("nomansland")
@Mixin(value = CauldronEvents.class, remap = false)
public class CauldronEventsBrewingCauldronMixin {

    @Inject(method = "onRightClickBlock", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$leaveBrewingCauldrons(UseItemOnBlockEvent event, CallbackInfo ci) {
        if (BuiltInRegistries.BLOCK.getKey(event.getLevel().getBlockState(event.getPos()).getBlock()).getNamespace().equals("toil_and_trouble")) {
            ci.cancel();
        }
    }
}
