package com.evandev.spicedcider.mixin.chimes;

import com.cicada.chimes.block.entity.WindChimeBE;
import com.evandev.spicedcider.compat.chimes.ChimesWindServer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("chimes")
@IfModLoaded("livingwind")
@Mixin(value = WindChimeBE.class, remap = false)
public class WindChimeBEWindMixin {

    @Inject(method = "broadcastAmbientChiming", at = @At("HEAD"))
    private void spicedcider$windGust(RandomSource random, CallbackInfo ci) {
        ChimesWindServer.tickChime((WindChimeBE) (Object) this, random);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private <T extends Entity> List<T> spicedcider$throttlePhantomScan(Level level, Class<T> type, AABB box, Operation<List<T>> original) {
        if (level.isClientSide || Math.floorMod(level.getGameTime() + ((BlockEntity) (Object) this).getBlockPos().asLong(), 20L) != 0) return List.of();
        return original.call(level, type, box);
    }
}
