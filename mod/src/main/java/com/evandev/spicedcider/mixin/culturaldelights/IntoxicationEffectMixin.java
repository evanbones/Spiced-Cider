package com.evandev.spicedcider.mixin.culturaldelights;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("culturaldelights")
@Mixin(targets = "com.baisylia.culturaldelights.effect.custom.IntoxicationEffect", remap = false)
public abstract class IntoxicationEffectMixin extends MobEffect {

    protected IntoxicationEffectMixin(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public MobEffectCategory getCategory() {
        return MobEffectCategory.HARMFUL;
    }
}
