package com.evandev.spicedcider.mixin.valhelsia.client;

import com.evandev.spicedcider.compat.valhelsia.client.TentHitbox;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("valhelsia_structures")
@Mixin(GameRenderer.class)
public class GameRendererTentPickMixin {
    @WrapOperation(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
    private HitResult spicedcider$pickTent(Entity entity, double range, float partialTick, boolean fluids, Operation<HitResult> original) {
        return TentHitbox.pick(entity, original.call(entity, range, partialTick, fluids), range, partialTick);
    }
}
