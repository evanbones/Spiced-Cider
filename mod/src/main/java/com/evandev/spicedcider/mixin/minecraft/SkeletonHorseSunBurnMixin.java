package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonHorse.class)
public abstract class SkeletonHorseSunBurnMixin extends AbstractHorse {
    protected SkeletonHorseSunBurnMixin(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void spicedcider$burnInSun(CallbackInfo ci) {
        if (!this.isAlive() || !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.skeletonHorsesBurn, false) || !this.isSunBurnTick()) return;
        ItemStack armor = this.getItemBySlot(EquipmentSlot.BODY);
        if (armor.isEmpty()) {
            this.igniteForSeconds(8.0F);
        } else if (armor.isDamageableItem()) {
            armor.setDamageValue(armor.getDamageValue() + this.random.nextInt(2));
            if (armor.getDamageValue() >= armor.getMaxDamage()) {
                this.onEquippedItemBroken(armor.getItem(), EquipmentSlot.BODY);
                this.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
            }
        }
    }
}
