package com.evandev.spicedcider.mixin.amendments;

import com.evandev.spicedcider.compat.firevariants.FireVariantCandles;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.amendments.events.behaviors.SkullCandleConversion;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("amendments")
@Mixin(SkullCandleConversion.class)
public abstract class SkullCandleConversionFireMixin {
    @ModifyReturnValue(method = "isCorrectCandle", at = @At("RETURN"))
    private static boolean spicedcider$fireCandles(boolean original, Item item) {
        return original || item instanceof BlockItem blockItem && FireVariantCandles.particle(blockItem.getBlock()) != null;
    }
}
