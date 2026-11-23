package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.item.BowFatigue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BowItem.class)
public abstract class BowItemFatigueMixin {
    @ModifyVariable(method = "shootProjectile", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private float spicedcider$fatigue(float inaccuracy, LivingEntity shooter, Projectile projectile, int index, float velocity, float originalInaccuracy, float angle, @Nullable LivingEntity target) {
        return BowFatigue.inaccuracy(inaccuracy, shooter.getTicksUsingItem());
    }

    @ModifyArg(method = "releaseUsing", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/BowItem;shoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Ljava/util/List;FFZLnet/minecraft/world/entity/LivingEntity;)V"),
            index = 7)
    private boolean spicedcider$noFatiguedCrit(boolean crit, @Local(argsOnly = true) LivingEntity shooter) {
        return BowFatigue.canCrit(crit, shooter.getTicksUsingItem());
    }
}
