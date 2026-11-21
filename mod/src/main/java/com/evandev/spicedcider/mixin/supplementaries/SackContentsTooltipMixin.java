package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.supplementaries.common.items.SackItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@IfModLoaded("supplementaries")
@Mixin(value = SackItem.class, remap = false)
public class SackContentsTooltipMixin {

    private static boolean spicedcider$hidden() {
        return SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.hideSackContentsTooltip, false);
    }

    @WrapOperation(method = "appendHoverText", at = @At(value = "INVOKE", target = "Lnet/mehvahdjukaar/supplementaries/common/utils/ItemsUtil;addShulkerLikeTooltips(Lnet/minecraft/world/item/ItemStack;Ljava/util/List;)V"))
    private void spicedcider$hideContentsList(ItemStack stack, List<Component> tooltip, Operation<Void> original) {
        if (!spicedcider$hidden()) {
            original.call(stack, tooltip);
        }
    }

    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void spicedcider$hideContentsPreview(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        if (spicedcider$hidden() && ModList.get().isLoaded("iteminteractions")) {
            cir.setReturnValue(Optional.empty());
        }
    }
}
