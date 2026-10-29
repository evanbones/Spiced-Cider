package com.evandev.spicedcider.mixin.strawgolem;

import com.evandev.spicedcider.content.handler.StrawGolemEvents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {

    @ModifyReturnValue(method = "canSpawnGolem", at = @At("RETURN"))
    private boolean spicedcider$canSpawnStrawGolem(boolean original, LevelReader level, BlockPos pos) {
        return original || StrawGolemEvents.basePattern().find(level, pos) != null;
    }

    @Inject(method = "trySpawnGolem", at = @At("HEAD"), cancellable = true)
    private void spicedcider$trySpawnStrawGolem(Level level, BlockPos pos, CallbackInfo ci) {
        if (StrawGolemEvents.trySpawn(level, pos)) {
            ci.cancel();
        }
    }
}
