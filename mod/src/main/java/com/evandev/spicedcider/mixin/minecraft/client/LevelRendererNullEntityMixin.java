package com.evandev.spicedcider.mixin.minecraft.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererNullEntityMixin {

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
    private Iterable<Entity> spicedcider$snapshotEntities(ClientLevel level, Operation<Iterable<Entity>> original) {
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : original.call(level)) {
            if (entity != null) entities.add(entity);
        }
        return entities;
    }
}
