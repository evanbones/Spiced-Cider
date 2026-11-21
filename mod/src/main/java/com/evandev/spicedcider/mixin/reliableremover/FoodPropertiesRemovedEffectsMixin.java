package com.evandev.spicedcider.mixin.reliableremover;

import com.evandev.reliable_remover.api.ReliableRemoverAPI;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@IfModLoaded("reliable_remover")
@Mixin(FoodProperties.class)
public class FoodPropertiesRemovedEffectsMixin {

    @ModifyReturnValue(method = "effects", at = @At("RETURN"))
    private List<FoodProperties.PossibleEffect> spicedcider$dropRemovedEffects(List<FoodProperties.PossibleEffect> effects) {
        if (effects.isEmpty()) return effects;
        boolean blocked = false;
        for (FoodProperties.PossibleEffect effect : effects) {
            if (ReliableRemoverAPI.isEffectCreativeBlocked(effect.effect().getEffect())) {
                blocked = true;
                break;
            }
        }
        if (!blocked) return effects;
        return effects.stream()
                .filter(effect -> !ReliableRemoverAPI.isEffectCreativeBlocked(effect.effect().getEffect()))
                .toList();
    }
}
