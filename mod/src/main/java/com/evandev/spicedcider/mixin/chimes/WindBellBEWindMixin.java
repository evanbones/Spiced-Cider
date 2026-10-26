package com.evandev.spicedcider.mixin.chimes;

import com.cicada.chimes.block.entity.WindBellBE;
import com.evandev.spicedcider.compat.chimes.ChimesWindServer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
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
@Mixin(value = WindBellBE.class, remap = false)
public class WindBellBEWindMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void spicedcider$windGust(CallbackInfo ci) {
        if (((WindBellBE) (Object) this).getLevel() == null) return;
        ChimesWindServer.tickBell((WindBellBE) (Object) this, ((WindBellBE) (Object) this).getLevel().getRandom());
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private <T extends Entity> List<T> spicedcider$throttlePhantomScan(Level level, Class<T> type, AABB box, Operation<List<T>> original) {
        if (level.isClientSide || Math.floorMod(level.getGameTime() + ((BlockEntity) (Object) this).getBlockPos().asLong(), 20L) != 0) return List.of();
        return original.call(level, type, box);
    }
}
