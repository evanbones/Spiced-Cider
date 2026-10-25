package com.evandev.spicedcider.mixin.obe;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import fr.madu59.obe.client.renderer.entity.ext.EntityExt;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("obe")
@IfModLoaded("amendments")
@Mixin(value = SignBlockEntity.class, priority = 1100)
public class HangingSignUnbakedMixin {

    @Inject(method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    private void spicedcider$keepHangingSignsAnimated(CallbackInfo ci) {
        if ((Object) this instanceof HangingSignBlockEntity) ((EntityExt) this).obe$isSupported(false);
    }
}
