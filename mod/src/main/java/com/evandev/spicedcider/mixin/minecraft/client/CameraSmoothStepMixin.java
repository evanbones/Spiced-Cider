package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.stepup.SmoothStep;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraSmoothStepMixin {

    @Shadow
    private Vec3 position;

    @Shadow
    protected abstract void setPosition(Vec3 pos);

    @Inject(method = "setup", at = @At("TAIL"))
    private void spicedcider$smoothStep(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if ((Object) this != mc.gameRenderer.getMainCamera()) return;
        if (detached || entity != mc.player) {
            SmoothStep.reset(position.y);
            return;
        }
        double y = SmoothStep.adjust(position.y);
        if (y != position.y) setPosition(new Vec3(position.x, y, position.z));
    }
}
