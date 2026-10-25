package com.evandev.spicedcider.mixin.wherewindsblow;

import com.jvn.wherewindsblow.client.foliage.ResponsiveFoliage;
import com.jvn.wherewindsblow.client.foliage.SnowRealMagicCompat;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@IfModLoaded("where_winds_blow")
@IfModLoaded("snowrealmagic")
@Mixin(targets = "snownee.snow.client.model.SnowVariantModel", remap = false)
public class SnowVariantModelSwayMixin {

    @Shadow
    @Final
    @Mutable
    private BakedModel variantModel;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void spicedcider$wrapVariant(BakedModel model, BakedModel variant, boolean required, CallbackInfo ci) {
        variantModel = ResponsiveFoliage.wrapSnowRealMagicVariant(variantModel);
    }

    @Inject(method = "emitBlockQuads", at = @At("HEAD"))
    private void spicedcider$beginRender(BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> random, @Coerce Object context, CallbackInfo ci) {
        if (pos != null) SnowRealMagicCompat.beginRender(level, pos);
    }

    @Inject(method = "emitBlockQuads", at = @At("RETURN"))
    private void spicedcider$endRender(BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> random, @Coerce Object context, CallbackInfo ci) {
        if (pos != null) SnowRealMagicCompat.endRender();
    }
}
