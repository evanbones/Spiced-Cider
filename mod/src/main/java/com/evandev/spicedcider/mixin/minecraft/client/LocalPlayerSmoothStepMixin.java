package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.stepup.SmoothStep;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerSmoothStepMixin {

    @WrapMethod(method = "move")
    private void spicedcider$detectStep(MoverType type, Vec3 movement, Operation<Void> original) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        double before = player.getY();
        boolean grounded = player.onGround();
        original.call(type, movement);
        if (type == MoverType.SELF && grounded) {
            SmoothStep.onMove(player.getY() - before - Math.max(0.0, movement.y));
        }
    }
}
