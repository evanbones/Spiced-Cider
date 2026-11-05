package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.level.blocks.HangingMossBlock;
import com.evandev.spicedcider.client.particle.CreakingEyesParticle;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModParticleTypes;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("vanillabackport")
@Mixin(HangingMossBlock.class)
public abstract class HangingMossCreakingEyesMixin {
    @Inject(method = "animateTick", at = @At("HEAD"))
    private void spicedcider$creakingEyes(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (random.nextInt(100) != 0 || !state.getValue(HangingMossBlock.TIP)) return;
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.creakingEyeParticles, true)) return;
        if (level.getMaxLocalRawBrightness(pos.below()) > CreakingEyesParticle.MAX_LIGHT) return;
        level.addParticle(ModParticleTypes.CREAKING_EYES.get(), pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
    }
}
