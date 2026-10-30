package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.compat.nomansland.CarvingWheelScreen;
import com.farcr.nomansland.client.gui.CarvingSelectionScreen;
import com.farcr.nomansland.common.carving.CarvingType;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("nomansland")
@Mixin(value = CarvingSelectionScreen.class, remap = false)
public class CarvingWheelMixin {

    @Inject(method = "open", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$openWheel(BlockPos clickedPos, Direction clickedFace, InteractionHand hand, List<CarvingType> types, CallbackInfo ci) {
        Minecraft.getInstance().setScreen(new CarvingWheelScreen(clickedPos, clickedFace, hand, types));
        ci.cancel();
    }
}
