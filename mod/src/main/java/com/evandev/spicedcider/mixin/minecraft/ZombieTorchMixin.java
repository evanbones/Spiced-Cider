package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieTorchMixin extends Monster {

    @Unique
    private static final float spicedcider$TORCH_CHANCE = 0.1F;

    protected ZombieTorchMixin(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void spicedcider$holdTorch(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        if (this.getMainHandItem().isEmpty() && random.nextFloat() < spicedcider$TORCH_CHANCE) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TORCH));
        }
    }
}
