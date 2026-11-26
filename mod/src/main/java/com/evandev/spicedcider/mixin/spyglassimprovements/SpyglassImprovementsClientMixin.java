package com.evandev.spicedcider.mixin.spyglassimprovements;

import com.evandev.spicedcider.util.CarriedItems;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("spyglass_improvements")
@Mixin(targets = "me.juancarloscp52.spyglass_improvements.client.SpyglassImprovementsClient", remap = false)
public class SpyglassImprovementsClientMixin {

    @Shadow
    public static int slot;

    @ModifyExpressionValue(
            method = "onClientTick(Lnet/minecraft/client/Minecraft;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isCreative()Z")
    )
    private boolean spicedcider$zoomWithCarriedSpyglass(boolean creative, @Local LocalPlayer player) {
        return creative || slot < 0 && CarriedItems.has(player, stack -> stack.is(Items.SPYGLASS));
    }
}
