package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.spicedcider.compat.reliablebackpacks.WornBackpackContainer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("reliable_backpacks")
@Mixin(Player.class)
public abstract class PlayerBackpackSlotMixin {
    @ModifyReturnValue(method = "canUseSlot", at = @At("RETURN"))
    private boolean spicedcider$keepFilledBackpackOn(boolean original, EquipmentSlot slot) {
        Player player = (Player) (Object) this;
        return original && !(slot == EquipmentSlot.CHEST && !player.isCreative() && WornBackpackContainer.hasContents(player.getItemBySlot(EquipmentSlot.CHEST)));
    }
}
