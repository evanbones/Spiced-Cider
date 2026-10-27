package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MapItemSavedData.class)
public class MapOffMapPlayerRotationMixin {
    @Shadow
    @Final
    public ResourceKey<Level> dimension;

    @ModifyArg(method = "addDecoration", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/saveddata/maps/MapDecoration;<init>(Lnet/minecraft/core/Holder;BBBLjava/util/Optional;)V"), index = 3)
    private byte spicedcider$offMapPlayerRotation(byte rotation, @Local(argsOnly = true) Holder<MapDecorationType> type, @Local(argsOnly = true) LevelAccessor level,
                                                @Local(argsOnly = true, ordinal = 2) double yRot) {
        if (!type.is(MapDecorationTypes.PLAYER_OFF_MAP) && !type.is(MapDecorationTypes.PLAYER_OFF_LIMITS)) return rotation;
        if (this.dimension == Level.NETHER && level != null) {
            int time = (int) (level.getLevelData().getDayTime() / 10L);
            return (byte) (time * time * 34187121 + time * 121 >> 15 & 15);
        }
        return (byte) ((int) ((yRot + (yRot < 0.0 ? -8.0 : 8.0)) * 16.0 / 360.0));
    }
}
