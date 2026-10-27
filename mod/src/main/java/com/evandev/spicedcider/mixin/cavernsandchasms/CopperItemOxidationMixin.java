package com.evandev.spicedcider.mixin.cavernsandchasms;

import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.caverns_and_chasms.common.item.copper.WeatheringCopperItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("caverns_and_chasms")
@Mixin(targets = "com.teamabnormals.caverns_and_chasms.core.other.CCEvents", remap = false)
public class CopperItemOxidationMixin {

    @WrapOperation(method = "onLivingTick", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/caverns_and_chasms/common/item/copper/WeatheringCopperItem;updateOxidation(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)V"))
    private static void spicedcider$skipRemovedStages(WeatheringCopperItem item, LivingEntity entity, EquipmentSlot slot, ItemStack stack, Level level, Operation<Void> original) {
        boolean removed = item.getNext(stack).map(next -> ReliableRemoverCompat.isRemoved(next.getItem())).orElse(false);
        if (!removed) {
            original.call(item, entity, slot, stack, level);
        }
    }
}
