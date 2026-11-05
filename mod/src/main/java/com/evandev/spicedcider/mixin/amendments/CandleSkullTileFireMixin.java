package com.evandev.spicedcider.mixin.amendments;

import com.evandev.spicedcider.compat.firevariants.FireVariantCandles;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.amendments.common.tile.CandleSkullBlockTile;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("amendments")
@Mixin(CandleSkullBlockTile.class)
public abstract class CandleSkullTileFireMixin {
    @Shadow
    private ParticleType<?> particle;

    @Inject(method = "setCandle", at = @At("TAIL"))
    private void spicedcider$fireParticle(BlockState candle, CallbackInfo ci) {
        if (candle == null) return;
        ParticleType<?> fireParticle = FireVariantCandles.particle(candle.getBlock());
        if (fireParticle != null) this.particle = fireParticle;
    }
}
