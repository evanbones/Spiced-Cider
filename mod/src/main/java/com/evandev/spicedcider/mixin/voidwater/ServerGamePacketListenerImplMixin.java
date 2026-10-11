package com.evandev.spicedcider.mixin.voidwater;

import com.evandev.spicedcider.voidwater.VoidTrails;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    private boolean clientIsFloating;

    @Shadow
    private int aboveGroundTickCount;

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleMovePlayer", at = @At("TAIL"))
    private void spicedcider$swimmingInVoidTrailIsNotFloating(CallbackInfo ci) {
        if (!clientIsFloating) return;
        Level level = player.level();
        if (VoidTrails.columns(level) == null) return;
        AABB box = player.getBoundingBox();
        int minY = level.getMinBuildHeight();
        if (box.maxY >= minY || box.maxY <= minY - VoidTrails.maxLength()) return;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = Mth.floor(box.minX); x < Mth.ceil(box.maxX); x++) {
            for (int z = Mth.floor(box.minZ); z < Mth.ceil(box.maxZ); z++) {
                if (!level.getFluidState(pos.set(x, minY, z)).isEmpty()) {
                    clientIsFloating = false;
                    aboveGroundTickCount = 0;
                    return;
                }
            }
        }
    }
}
