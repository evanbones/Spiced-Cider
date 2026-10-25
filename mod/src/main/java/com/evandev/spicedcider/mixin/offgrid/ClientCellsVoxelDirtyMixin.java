package com.evandev.spicedcider.mixin.offgrid;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.client.ClientCells;
import net.mehvahdjukaar.polytone.api.VoxelVolumeApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("offgrid")
@IfModLoaded("polytone")
@Mixin(value = ClientCells.class, remap = false)
public class ClientCellsVoxelDirtyMixin {

    @Inject(method = "markDirty", at = @At("TAIL"))
    private static void spicedcider$refreshColoredLight(Level level, BlockPos pos, CallbackInfo ci) {
        VoxelVolumeApi.markDirty(pos);
    }
}
