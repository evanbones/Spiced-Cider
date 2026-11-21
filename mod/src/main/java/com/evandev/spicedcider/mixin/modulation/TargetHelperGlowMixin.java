package com.evandev.spicedcider.mixin.modulation;

import com.evandev.spicedcider.content.handler.ThreatGlowEvents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("modulation")
@Mixin(targets = "com.evandev.modulation.util.TargetHelper", remap = false)
public class TargetHelperGlowMixin {

    @ModifyReturnValue(method = "isLootContainerBlocked", at = @At("RETURN"))
    private static boolean spicedcider$glowBlockers(boolean blocked, Level level, BlockPos pos, Player player) {
        if (blocked) {
            ThreatGlowEvents.glowTargeting(level, player);
        }
        return blocked;
    }
}
