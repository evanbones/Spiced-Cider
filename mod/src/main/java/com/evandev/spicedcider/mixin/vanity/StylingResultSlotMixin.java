package com.evandev.spicedcider.mixin.vanity;

import com.evandev.spicedcider.interfaces.ITrimStylingMenu;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.vanity.common.menu.StylingMenu;

@IfModLoaded("vanity")
@Mixin(targets = "tech.thatgravyboat.vanity.common.menu.StylingMenu$2", remap = false)
public class StylingResultSlotMixin {

    @Shadow
    @Final
    StylingMenu this$0;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void spicedcider$consumeAddition(Player player, ItemStack stack, CallbackInfo ci) {
        ((ITrimStylingMenu) this.this$0).spicedcider$beforeResultTaken(player);
    }

    @Inject(method = "onTake", at = @At("TAIL"))
    private void spicedcider$refresh(Player player, ItemStack stack, CallbackInfo ci) {
        ((ITrimStylingMenu) this.this$0).spicedcider$afterResultTaken();
    }
}
