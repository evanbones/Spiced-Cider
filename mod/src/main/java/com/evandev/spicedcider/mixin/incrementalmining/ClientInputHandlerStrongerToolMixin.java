package com.evandev.spicedcider.mixin.incrementalmining;

import com.evandev.spicedcider.client.events.InsufficientToolHandler;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("incrementalmining")
@Mixin(targets = "com.jdynamo.incrementalmining.client.ClientInputHandler", remap = false)
public class ClientInputHandlerStrongerToolMixin {

    @WrapOperation(method = "onClientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
    private static boolean spicedcider$noSwingsOnUnmineable(KeyMapping key, Operation<Boolean> original) {
        boolean down = original.call(key);
        if (!down || !SpicedCiderConfig.CLIENT.unmineableBlockSparks.get()) {
            return down;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.player.isCreative()
                || !(minecraft.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return down;
        }
        return !InsufficientToolHandler.requiresStrongerTool(minecraft.player, minecraft.level.getBlockState(hit.getBlockPos()), minecraft.player.getMainHandItem());
    }
}
