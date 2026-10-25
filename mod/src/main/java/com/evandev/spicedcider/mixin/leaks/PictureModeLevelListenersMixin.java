package com.evandev.spicedcider.mixin.leaks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("picturemode")
@Mixin(targets = "mod.icanttellyou.picturemode.forgelike.client.event.listeners.LevelEventListeners", remap = false)
public class PictureModeLevelListenersMixin {
    @Shadow
    private LevelAccessor recentlyConstructedLevel;

    @Inject(method = "onLevelUnload", at = @At("TAIL"))
    private void spicedcider$forgetLevel(LevelEvent.Unload event, CallbackInfo ci) {
        if (recentlyConstructedLevel == event.getLevel()) recentlyConstructedLevel = null;
    }
}
