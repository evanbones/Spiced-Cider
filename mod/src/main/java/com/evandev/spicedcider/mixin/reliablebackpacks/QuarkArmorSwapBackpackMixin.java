package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.management.module.ExpandedItemInteractionsModule;

@IfModLoaded("quark")
@Mixin(value = ExpandedItemInteractionsModule.class, remap = false)
public class QuarkArmorSwapBackpackMixin {
    @ModifyExpressionValue(
            method = "armorOverride",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;mayPickup(Lnet/minecraft/world/entity/player/Player;)Z", remap = true)
    )
    private static boolean spicedcider$respectLockedArmorSlot(boolean original, @Local(argsOnly = true) Player player, @Local EquipmentSlot equipSlot) {
        return original && player.canUseSlot(equipSlot);
    }
}
