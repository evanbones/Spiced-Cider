package com.evandev.spicedcider.mixin.controlify;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.api.ControlifyApi;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BooleanSupplier;

@IfModLoaded("controlify")
@Mixin(ToggleKeyMapping.class)
public class ToggleKeyMappingModdedToggleMixin {

    @Shadow
    @Final
    private BooleanSupplier needsToggle;

    @WrapMethod(method = "setDown")
    private void spicedcider$keepModdedToggle(boolean value, Operation<Void> original) {
        Options options = Minecraft.getInstance().options;
        boolean vanilla = (Object) this == options.keySprint || (Object) this == options.keyShift;
        if (vanilla || !needsToggle.getAsBoolean() || !ControlifyApi.get().currentInputMode().isController()) {
            original.call(value);
            return;
        }
        if (value) original.call(!((KeyMapping) (Object) this).isDown());
    }
}
