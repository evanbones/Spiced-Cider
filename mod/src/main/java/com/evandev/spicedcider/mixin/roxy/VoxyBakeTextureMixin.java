package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.polytone.PolytoneLodLight;
import com.evandev.spicedcider.compat.voxy.VoxyBakeTexels;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.client.core.model.bakery.SoftwareModelTextureBakery", remap = false)
public class VoxyBakeTextureMixin {
    @Unique
    private static final boolean spicedcider$BIG_WATER = ModList.get().isLoaded("bigwater");
    @Unique
    private static final boolean spicedcider$POLYTONE = ModList.get().isLoaded("polytone");
    @Unique
    private static final float spicedcider$FLUID_CONTRAST = 0.3F;

    @Inject(method = "renderToOutput", at = @At("RETURN"))
    private void spicedcider$adjustBake(BlockState state, long outputBuffer, CallbackInfoReturnable<Integer> cir) {
        if (spicedcider$BIG_WATER && state.getBlock() instanceof LiquidBlock) {
            VoxyBakeTexels.flatten(outputBuffer, spicedcider$FLUID_CONTRAST);
        }
        if (spicedcider$POLYTONE && state.getLightEmission() > 0) {
            int color = PolytoneLodLight.lightColor(state);
            if (color >= 0) VoxyBakeTexels.tintByLight(outputBuffer, color);
        }
    }
}
