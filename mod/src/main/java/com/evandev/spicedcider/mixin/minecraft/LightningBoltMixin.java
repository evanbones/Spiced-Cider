package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public class LightningBoltMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$onInit(net.minecraft.world.entity.EntityType<?> type, Level level, CallbackInfo ci) {
        LightningBolt bolt = (LightningBolt) (Object) this;
        if (!level.isClientSide()) {
            BlockPos pos = bolt.blockPosition();
            if (level.getBlockState(pos).isAir()) {
                level.setBlock(pos, ModBlocks.LIGHTNING_LIGHT.get().defaultBlockState(), 3);
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;discard()V"))
    private void spicedcider$onDiscard(CallbackInfo ci) {
        LightningBolt bolt = (LightningBolt) (Object) this;
        if (!bolt.level().isClientSide()) {
            BlockPos pos = bolt.blockPosition();
            if (bolt.level().getBlockState(pos).is(ModBlocks.LIGHTNING_LIGHT.get())) {
                bolt.level().removeBlock(pos, false);
            }
        }
    }
}