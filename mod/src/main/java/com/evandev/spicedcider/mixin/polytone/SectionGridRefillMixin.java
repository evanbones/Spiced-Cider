package com.evandev.spicedcider.mixin.polytone;

import com.evandev.spicedcider.compat.polytone.ClientChunkEpoch;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.polytone.content.shaders.voxel.SectionGrid;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.LongConsumer;

@IfModLoaded("polytone")
@Mixin(value = SectionGrid.class, remap = false)
public abstract class SectionGridRefillMixin {

    @Shadow
    private SectionPos originSection;

    @Unique
    private boolean spicedcider$clean;
    @Unique
    private long spicedcider$cleanOrigin;
    @Unique
    private int spicedcider$cleanEpoch;
    @Unique
    private int spicedcider$cleanGeneration;
    @Unique
    private int spicedcider$passEpoch;
    @Unique
    private int spicedcider$passGeneration;

    @Inject(method = "refillStaleSections", at = @At("HEAD"), cancellable = true)
    private void spicedcider$skipWhenUnchanged(ClientLevel level, long budgetNanos, LongConsumer onSectionFilled, CallbackInfoReturnable<Integer> cir) {
        int epoch = ClientChunkEpoch.get();
        int generation = spicedcider$generation();
        if (spicedcider$clean && spicedcider$cleanOrigin == originSection.asLong()
                && spicedcider$cleanEpoch == epoch && spicedcider$cleanGeneration == generation) {
            cir.setReturnValue(0);
            return;
        }
        spicedcider$passEpoch = epoch;
        spicedcider$passGeneration = generation;
    }

    @Inject(method = "refillStaleSections", at = @At(value = "RETURN", ordinal = 0))
    private void spicedcider$outOfBudget(ClientLevel level, long budgetNanos, LongConsumer onSectionFilled, CallbackInfoReturnable<Integer> cir) {
        spicedcider$clean = false;
    }

    @Inject(method = "refillStaleSections", at = @At(value = "RETURN", ordinal = 1))
    private void spicedcider$completed(ClientLevel level, long budgetNanos, LongConsumer onSectionFilled, CallbackInfoReturnable<Integer> cir) {
        spicedcider$clean = true;
        spicedcider$cleanOrigin = originSection.asLong();
        spicedcider$cleanEpoch = spicedcider$passEpoch;
        spicedcider$cleanGeneration = spicedcider$passGeneration;
    }

    @Unique
    private int spicedcider$generation() {
        return (Object) this instanceof PaletteGridAccessor grid ? grid.spicedcider$palette().dynamicGeneration() : 0;
    }
}
