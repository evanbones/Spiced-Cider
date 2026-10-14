package com.evandev.spicedcider.mixin.cavernsandchasms;

import com.evandev.spicedcider.util.TuningForkNotes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.caverns_and_chasms.core.other.CCEvents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("caverns_and_chasms")
@Mixin(value = CCEvents.class, remap = false)
public class TuningForkHeldMixin {

    @WrapOperation(
            method = "onLivingTick",
            at = @At(value = "INVOKE", target = "Lcom/teamabnormals/caverns_and_chasms/common/item/copper/TuningForkItem;isTuningForkWithNote(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private static boolean spicedcider$countSpelunkeryFork(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack) || TuningForkNotes.isFork(stack);
    }
}
