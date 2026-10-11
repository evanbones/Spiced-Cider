package com.evandev.spicedcider.mixin.leaks;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@IfModLoaded("cold_sweat")
@Mixin(value = FoodData.class, priority = 1500)
public class ColdSweatStoredPlayerMixin {
    @Shadow(remap = false)
    private static Player STORED_PLAYER;

    @WrapMethod(method = "tick")
    private void spicedcider$releaseStoredPlayer(Player player, Operation<Void> original) {
        try {
            original.call(player);
        } finally {
            STORED_PLAYER = null;
        }
    }
}
