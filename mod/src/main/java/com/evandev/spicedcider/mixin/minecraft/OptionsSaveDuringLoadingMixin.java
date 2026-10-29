package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Options;
import net.neoforged.neoforge.client.loading.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Options.class)
public class OptionsSaveDuringLoadingMixin {

    @WrapMethod(method = "save")
    private void spicedcider$keepModdedKeysUntilLoaded(Operation<Void> original) {
        if (!ClientModLoader.isLoading()) original.call();
    }
}
