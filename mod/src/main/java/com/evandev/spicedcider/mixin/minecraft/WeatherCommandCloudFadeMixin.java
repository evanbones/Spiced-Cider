package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.weather.CloudCoverData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.WeatherCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WeatherCommand.class)
public class WeatherCommandCloudFadeMixin {

    @Inject(method = {"setClear", "setRain", "setThunder"}, at = @At("HEAD"))
    private static void spicedcider$fastCloudFade(CommandSourceStack source, int time, CallbackInfoReturnable<Integer> cir) {
        CloudCoverData.get(source.getServer().overworld()).fastFade();
    }
}
