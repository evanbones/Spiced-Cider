package com.evandev.spicedcider.mixin.horseman;

import com.evandev.spicedcider.compat.horseman.TranslucentMounts;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("horseman")
@Mixin(LevelRenderer.class)
public abstract class LevelRendererTranslucentMountMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    protected abstract void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource);

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
    private void spicedcider$deferTranslucentMount(LevelRenderer self, Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, Operation<Void> original) {
        if (bufferSource == this.renderBuffers.bufferSource() && TranslucentMounts.defer(entity, camX, camY, camZ, partialTick)) {
            return;
        }
        original.call(self, entity, camX, camY, camZ, partialTick, poseStack, bufferSource);
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/Camera;)V"))
    private void spicedcider$renderTranslucentMountLast(CallbackInfo ci) {
        TranslucentMounts.Deferred mount = TranslucentMounts.take();
        if (mount == null) {
            return;
        }
        MultiBufferSource.BufferSource buffers = this.renderBuffers.bufferSource();
        buffers.endBatch();
        this.renderEntity(mount.entity(), mount.camX(), mount.camY(), mount.camZ(), mount.partialTick(), new PoseStack(), buffers);
        buffers.endBatch();
    }
}
