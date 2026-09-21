package com.evandev.spicedcider.mixin.sodium;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.caffeinemc.mods.sodium.client.model.light.flat.FlatLightPipeline;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("sodium")
@Mixin(value = FlatLightPipeline.class, remap = false)
public abstract class FlatLightPipelineMixin {

    @Shadow
    @Final
    private LightDataAccess lightCache;

    @Inject(method = "getOffsetLightmap", at = @At("HEAD"), cancellable = true)
    private void spicedcider$offsetLightmapWithSubLevelPrecision(BlockPos pos, Direction face, CallbackInfoReturnable<Integer> cir) {
        int word = this.lightCache.get(pos);

        if (LightDataAccess.unpackEM(word)) {
            cir.setReturnValue(LightTexture.FULL_BRIGHT);
            return;
        }

        int adjWord = this.lightCache.get(pos, face);

        int bl = (LightDataAccess.unpackBL(adjWord) << 4) | ((adjWord >>> 24) & 0xF);
        int lu = LightDataAccess.unpackLU(word) << 4;

        cir.setReturnValue(Math.max(bl, lu) | (LightDataAccess.unpackSL(adjWord) << 20));
    }
}
