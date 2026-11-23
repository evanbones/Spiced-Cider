package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.stepup.SmoothStep;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class EntitySmoothStepMixin {

    @WrapMethod(method = "move")
    private void spicedcider$detectStep(MoverType type, Vec3 movement, Operation<Void> original) {
        Entity self = (Entity) (Object) this;
        if (type != MoverType.SELF || !self.level().isClientSide) {
            original.call(type, movement);
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || (self != player && self != player.getRootVehicle())) {
            original.call(type, movement);
            return;
        }
        double before = self.getY();
        boolean grounded = self.onGround();
        original.call(type, movement);
        SmoothStep.onMove(grounded, self.onGround(), self.getY() - before - Math.max(0.0, movement.y), movement.y);
    }
}
