package com.evandev.spicedcider.mixin.caverns_and_chasms;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.caverns_and_chasms.common.item.PackingContainerItem;
import com.teamabnormals.caverns_and_chasms.common.item.component.PackingContainerContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackingContainerItem.class)
public abstract class PackingContainerItemMixin {

    @WrapOperation(
            method = "overrideStackedOnOther",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/teamabnormals/caverns_and_chasms/common/item/component/PackingContainerContents$Mutable;canAddStack(Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean spicedcider$testTheClickedStack(PackingContainerContents.Mutable contents, ItemStack container, Operation<Boolean> original, ItemStack stack, Slot slot, ClickAction action, Player player) {
        return original.call(contents, slot.getItem());
    }
}
