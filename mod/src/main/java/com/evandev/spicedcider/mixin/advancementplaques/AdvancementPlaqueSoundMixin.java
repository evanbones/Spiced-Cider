package com.evandev.spicedcider.mixin.advancementplaques;

import com.anthonyhilyard.advancementplaques.ui.render.AdvancementPlaque;
import com.evandev.spicedcider.compat.advancementplaques.PlaqueSounds;
import com.evandev.spicedcider.mixin.minecraft.AdvancementToastAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("advancementplaques")
@Mixin(value = AdvancementPlaque.class, remap = false)
public class AdvancementPlaqueSoundMixin {

    @Shadow
    @Final
    private AdvancementToast toast;

    @WrapOperation(method = "lambda$drawPlaque$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forUI(Lnet/minecraft/sounds/SoundEvent;FF)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"))
    private SimpleSoundInstance spicedcider$advancementSound(SoundEvent sound, float pitch, float volume, Operation<SimpleSoundInstance> original) {
        SoundEvent replaced = PlaqueSounds.forAdvancement(((AdvancementToastAccessor) toast).spicedcider$getAdvancement().id(), sound);
        return original.call(replaced, pitch, volume);
    }
}
