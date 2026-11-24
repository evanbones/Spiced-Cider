package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.create.BasinCasting;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("create")
@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinCastingMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void spicedcider$castInBasin(CallbackInfo ci) {
        BasinCasting.tick((BasinBlockEntity) (Object) this);
    }
}
