package com.evandev.spicedcider.mixin.minecraft;

import com.google.common.collect.Iterables;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(PotionContents.class)
public class PotionContentsBrewedMixin {

    @Shadow
    @Final
    private Optional<Holder<Potion>> potion;

    @Shadow
    @Final
    private List<MobEffectInstance> customEffects;

    @ModifyReturnValue(method = "getAllEffects", at = @At("RETURN"))
    private Iterable<MobEffectInstance> spicedcider$customOverridesBase(Iterable<MobEffectInstance> effects) {
        if (this.potion.isEmpty() || this.customEffects.isEmpty()) {
            return effects;
        }
        return Iterables.concat(Iterables.filter(this.potion.get().value().getEffects(), this::spicedcider$notOverridden), this.customEffects);
    }

    @Inject(method = "forEachEffect", at = @At("HEAD"), cancellable = true)
    private void spicedcider$forEachOverridden(Consumer<MobEffectInstance> action, CallbackInfo ci) {
        if (this.potion.isEmpty() || this.customEffects.isEmpty()) {
            return;
        }
        for (MobEffectInstance effect : this.potion.get().value().getEffects()) {
            if (this.spicedcider$notOverridden(effect)) {
                action.accept(new MobEffectInstance(effect));
            }
        }
        for (MobEffectInstance effect : this.customEffects) {
            action.accept(new MobEffectInstance(effect));
        }
        ci.cancel();
    }

    @Unique
    private boolean spicedcider$notOverridden(MobEffectInstance base) {
        for (MobEffectInstance custom : this.customEffects) {
            if (custom.getEffect().equals(base.getEffect())) {
                return false;
            }
        }
        return true;
    }
}
