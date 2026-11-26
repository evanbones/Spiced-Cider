package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.item.BreakRemainders;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityBreakRemainderMixin {

    @Inject(method = "onEquippedItemBroken", at = @At("TAIL"))
    private void spicedcider$leaveRemainder(Item item, EquipmentSlot slot, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level().isClientSide() || !entity.getItemBySlot(slot).isEmpty()) return;
        ItemStack remainder = BreakRemainders.remainderFor(item);
        if (!remainder.isEmpty()) entity.setItemSlot(slot, remainder);
    }
}
