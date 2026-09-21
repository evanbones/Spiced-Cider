package com.evandev.spicedcider.mixin.sodium;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.caffeinemc.mods.sodium.client.services.PlatformBlockAccess;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("sodium")
@Mixin(value = LightDataAccess.class, remap = false)
public abstract class LightDataAccessMixin {

    @Shadow
    @Final
    private BlockPos.MutableBlockPos pos;

    @Shadow
    protected BlockAndTintGetter level;

    @Inject(method = "packAO", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$packAoNarrow(float ao, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(((int) (Mth.clamp(ao, 0.0f, 1.0f) * 2048.0f) & 0xFFF) << 12);
    }

    @Inject(method = "unpackAO", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$unpackAoNarrow(int word, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(((word >>> 12) & 0xFFF) * (1.0f / 2048.0f));
    }

    @Inject(method = "getLightmap", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$getLightmapWithSubLevelPrecision(int word, CallbackInfoReturnable<Integer> cir) {
        int bl = (LightDataAccess.unpackBL(word) << 4) | ((word >>> 24) & 0xF);
        int lu = LightDataAccess.unpackLU(word) << 4;

        cir.setReturnValue(Math.max(bl, lu) | (LightDataAccess.unpackSL(word) << 20));
    }

    @Inject(method = "compute", at = @At("HEAD"), cancellable = true)
    private void spicedcider$computeWithSubLevelPrecision(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        BlockPos pos = this.pos.set(x, y, z);
        BlockAndTintGetter level = this.level;

        BlockState state = level.getBlockState(pos);

        boolean em = state.emissiveRendering(level, pos);
        boolean op = state.isViewBlocking(level, pos) && state.getLightBlock(level, pos) != 0;
        boolean fo = state.isSolidRender(level, pos);
        boolean fc = state.isCollisionShapeFullBlock(level, pos);

        int lu = PlatformBlockAccess.getInstance().getLightEmission(state, level, pos);

        int bl;
        int sl;
        int blFraction;

        if (fo && lu == 0) {
            bl = 0;
            sl = 0;
            blFraction = 0;
        } else if (em) {
            bl = level.getBrightness(LightLayer.BLOCK, pos);
            sl = level.getBrightness(LightLayer.SKY, pos);
            blFraction = 0;
        } else {
            int light = LevelRenderer.getLightColor(level, state, pos);
            bl = LightTexture.block(light);
            sl = LightTexture.sky(light);
            blFraction = light & 0xF;
        }

        float ao = state.getShadeBrightness(level, pos);

        cir.setReturnValue(LightDataAccess.packFC(fc)
                | LightDataAccess.packFO(fo)
                | LightDataAccess.packOP(op)
                | LightDataAccess.packEM(em)
                | LightDataAccess.packAO(ao)
                | LightDataAccess.packLU(lu)
                | LightDataAccess.packSL(sl)
                | LightDataAccess.packBL(bl)
                | (blFraction << 24));
    }
}
