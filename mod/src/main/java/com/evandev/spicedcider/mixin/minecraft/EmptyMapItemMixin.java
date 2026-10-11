package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.registry.ModDataComponents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EmptyMapItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EmptyMapItem.class)
public class EmptyMapItemMixin {
    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/MapItem;create(Lnet/minecraft/world/level/Level;IIBZZ)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack spicedcider$recordMapHeight(Level level, int x, int z, byte scale, boolean trackingPosition, boolean unlimitedTracking,
                                                  Operation<ItemStack> original, Level useLevel, Player player, InteractionHand hand) {
        ItemStack map = original.call(level, x, z, scale, trackingPosition, unlimitedTracking);
        map.set(ModDataComponents.MAP_HEIGHT, player.getBlockY());
        return map;
    }
}
