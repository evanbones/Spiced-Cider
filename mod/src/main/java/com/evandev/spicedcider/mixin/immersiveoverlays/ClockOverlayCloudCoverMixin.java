package com.evandev.spicedcider.mixin.immersiveoverlays;

import com.evandev.spicedcider.weather.CloudCover;
import com.evandev.spicedcider.weather.client.CloudCoverClient;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("immersiveoverlays")
@Mixin(targets = "cc.cassian.immersiveoverlays.overlay.ClockOverlay", remap = false)
public class ClockOverlayCloudCoverMixin {

    @ModifyReturnValue(method = "getWeather", at = @At("RETURN"))
    private static String spicedcider$cloudCoverIcon(String weather, Player player) {
        CloudCover cover = CloudCoverClient.currentCover();
        if (cover == null || cover == CloudCover.CLEAR) return weather;
        String body = switch (weather) {
            case "sun", "sunrise" -> "sun";
            case "moon", "moonrise" -> "moon";
            default -> null;
        };
        if (body == null) return weather;
        return switch (cover) {
            case MOSTLY_CLEAR -> "spicedcider/" + body + "_mostly_clear";
            case PARTLY_CLOUDY -> "spicedcider/" + body + "_partly_cloudy";
            default -> "spicedcider/cloudy";
        };
    }
}
