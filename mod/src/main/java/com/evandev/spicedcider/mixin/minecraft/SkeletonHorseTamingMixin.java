package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SkeletonHorse.class)
public abstract class SkeletonHorseTamingMixin extends AbstractHorse {

    protected SkeletonHorseTamingMixin(EntityType<? extends AbstractHorse> type, Level level) {
        super(type, level);
    }

    @WrapMethod(method = "mobInteract")
    private InteractionResult spicedcider$tameLikeHorse(Player player, InteractionHand hand, Operation<InteractionResult> original) {
        if (this.isTamed() || this.isBaby() || this.isVehicle()
                || !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.tameableSkeletonHorses, false)) {
            return original.call(player, hand);
        }
        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty() && this.isFood(held)) {
            return this.fedFood(player, held);
        }
        this.setPersistenceRequired();
        this.doPlayerRide(player);
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }
}
