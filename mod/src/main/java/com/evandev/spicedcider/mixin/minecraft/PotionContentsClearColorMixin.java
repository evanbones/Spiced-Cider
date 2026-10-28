package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.brewing.CauldronBrewing;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(PotionContents.class)
public class PotionContentsClearColorMixin {

    @Shadow
    @Final
    private Optional<Holder<Potion>> potion;

    @Shadow
    @Final
    private Optional<Integer> customColor;

    @Shadow
    @Final
    private List<MobEffectInstance> customEffects;

    @ModifyReturnValue(method = "getColor()I", at = @At("RETURN"))
    private int spicedcider$clearColor(int color) {
        if (this.customColor.isEmpty() && this.customEffects.isEmpty() && CauldronBrewing.isClear(this.potion)) {
            return CauldronBrewing.CLEAR_COLOR;
        }
        return color;
    }

    @ModifyReturnValue(method = "getColor(Lnet/minecraft/core/Holder;)I", at = @At("RETURN"))
    private static int spicedcider$clearColor(int color, Holder<Potion> potion) {
        return CauldronBrewing.isClear(Optional.of(potion)) ? CauldronBrewing.CLEAR_COLOR : color;
    }
}
