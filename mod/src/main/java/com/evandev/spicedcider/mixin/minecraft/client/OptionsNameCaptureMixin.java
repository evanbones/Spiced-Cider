package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.options.PackDefaults;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Options.class)
public class OptionsNameCaptureMixin {

    @ModifyArg(method = "processDumpedOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options$OptionAccess;process(Ljava/lang/String;Lnet/minecraft/client/OptionInstance;)V"), index = 1)
    private OptionInstance<?> spicedcider$captureDumpedName(String name, OptionInstance<?> option) {
        PackDefaults.record(name, option);
        return option;
    }

    @ModifyArg(method = {"processOptions", "processOptionsForge"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options$FieldAccess;process(Ljava/lang/String;Lnet/minecraft/client/OptionInstance;)V"), index = 1)
    private OptionInstance<?> spicedcider$captureName(String name, OptionInstance<?> option) {
        PackDefaults.record(name, option);
        return option;
    }
}
