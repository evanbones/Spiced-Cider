package com.evandev.spicedcider.mixin.tinytakeoverbackport;

import com.evandev.spicedcider.compat.tinytakeover.GoldenDandelions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("tiny_takeover_backport")
@Mixin(TemptingSensor.class)
public abstract class TemptingSensorGoldenDandelionMixin {
    @Unique
    private PathfinderMob spicedcider$sensingMob;

    @WrapMethod(method = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;)V")
    private void spicedcider$trackMob(ServerLevel level, PathfinderMob entity, Operation<Void> original) {
        this.spicedcider$sensingMob = entity;
        try {
            original.call(level, entity);
        } finally {
            this.spicedcider$sensingMob = null;
        }
    }

    @ModifyReturnValue(method = "playerHoldingTemptation", at = @At("RETURN"))
    private boolean spicedcider$goldenDandelion(boolean original, Player player) {
        return original || this.spicedcider$sensingMob != null && GoldenDandelions.tempts(this.spicedcider$sensingMob, player);
    }
}
