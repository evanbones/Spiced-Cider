package com.evandev.spicedcider.mixin.tide;

import com.evandev.spicedcider.compat.smoothanimations.SmoothLanding;
import com.li64.tide.registries.entities.misc.LootCrateEntity;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("tide")
@IfModLoaded("smooth_animations")
@Mixin(value = LootCrateEntity.class, remap = false)
public class LootCrateLandingMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void spicedcider$finishSmoothLanding(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.level() instanceof ClientLevel level && SmoothLanding.isLanding(self)) {
            level.removeEntity(self.getId(), Entity.RemovalReason.DISCARDED);
            ci.cancel();
        }
    }
}
