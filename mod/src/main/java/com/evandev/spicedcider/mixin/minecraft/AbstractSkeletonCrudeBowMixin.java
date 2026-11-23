package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.content.item.CrudeBowItem;
import com.evandev.spicedcider.registry.ModAttachments;
import com.evandev.spicedcider.registry.ModItems;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonCrudeBowMixin {
    @WrapOperation(method = "populateDefaultEquipmentSlots", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/AbstractSkeleton;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"))
    private void spicedcider$crudeBow(AbstractSkeleton skeleton, EquipmentSlot slot, ItemStack stack, Operation<Void> original) {
        if (stack.is(Items.BOW) && skeleton.getType() == EntityType.SKELETON && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.crudeBowSkeletons, false)) {
            stack = new ItemStack(ModItems.CRUDE_BOW.get());
        }
        original.call(skeleton, slot, stack);
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shoot(DDDFF)V"))
    private void spicedcider$crudeVelocity(AbstractArrow arrow, double x, double y, double z, float velocity, float inaccuracy, Operation<Void> original) {
        if (arrow.getData(ModAttachments.CRUDE_ARROW)) velocity *= CrudeBowItem.VELOCITY_MULTIPLIER;
        original.call(arrow, x, y, z, velocity, inaccuracy);
    }
}
