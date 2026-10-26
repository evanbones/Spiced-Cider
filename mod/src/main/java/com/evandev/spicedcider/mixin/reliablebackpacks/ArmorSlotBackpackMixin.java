package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackContainer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("reliable_backpacks")
@Mixin(targets = "net.minecraft.world.inventory.ArmorSlot")
public abstract class ArmorSlotBackpackMixin extends Slot {
    @Shadow
    @Final
    private EquipmentSlot slot;

    public ArmorSlotBackpackMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @ModifyReturnValue(method = "mayPickup", at = @At("RETURN"))
    private boolean spicedcider$keepFilledBackpackOn(boolean original, Player player) {
        return original && !(this.slot == EquipmentSlot.CHEST && !player.isCreative() && WornBackpackContainer.hasContents(this.getItem()));
    }
}
