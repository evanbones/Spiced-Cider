package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererLocalBossSoundsMixin {
    @Shadow
    private ClientLevel level;

    @Inject(method = "globalLevelEvent", at = @At("HEAD"), cancellable = true)
    private void spicedcider$localBossSounds(int type, BlockPos pos, int data, CallbackInfo ci) {
        if (this.level == null || !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.localizedBossSounds, false)) {
            return;
        }
        SoundEvent sound;
        float volume;
        switch (type) {
            case 1023 -> {
                sound = SoundEvents.WITHER_SPAWN;
                volume = 4.0F;
            }
            case 1038 -> {
                sound = SoundEvents.END_PORTAL_SPAWN;
                volume = 4.0F;
            }
            case 1028 -> {
                sound = SoundEvents.ENDER_DRAGON_DEATH;
                volume = 5.0F;
            }
            default -> {
                return;
            }
        }
        this.level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, SoundSource.HOSTILE, volume, 1.0F, false);
        ci.cancel();
    }
}
