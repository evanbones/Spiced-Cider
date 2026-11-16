package com.evandev.spicedcider.mixin.polytone;

import com.evandev.spicedcider.compat.polytone.HeldLightColors;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.polytone.content.shaders.light.ColoredLightsManager;
import net.mehvahdjukaar.polytone.content.shaders.light.PointLightProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("polytone")
@Mixin(value = ColoredLightsManager.class, remap = false)
public class ColoredLightsHeldItemMixin {

    @Inject(method = "lightOfStack", at = @At("HEAD"), cancellable = true)
    private void spicedcider$matchPlacedColor(ItemStack stack, CallbackInfoReturnable<PointLightProvider<Entity>> cir) {
        if (stack.isEmpty() || !SpicedCiderConfig.CLIENT.matchHeldLightColors.get()) return;
        PointLightProvider<Entity> provider = HeldLightColors.blockColored((ColoredLightsManager) (Object) this, stack);
        if (provider != null) cir.setReturnValue(provider);
    }
}
