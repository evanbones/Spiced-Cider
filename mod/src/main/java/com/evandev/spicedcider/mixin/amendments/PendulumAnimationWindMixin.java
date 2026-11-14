package com.evandev.spicedcider.mixin.amendments;

import com.evandev.spicedcider.wind.client.ClientWind;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.amendments.common.PendulumAnimation;
import net.mehvahdjukaar.amendments.common.SwingAnimation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@IfModLoaded("amendments")
@Mixin(value = PendulumAnimation.class, remap = false)
public abstract class PendulumAnimationWindMixin extends SwingAnimation {
    @Unique
    private static final Vector3f spicedcider$UP = new Vector3f(0.0F, 1.0F, 0.0F);
    @Unique
    private static final float TORQUE_PER_STRENGTH = 3.5F;
    @Unique
    private static final float TURBULENCE_TORQUE = 6.0F;
    @Unique
    private static final float SECONDS_PER_TICK = 0.05F;

    @Shadow
    private float angularVel;

    @Shadow
    private boolean hasDrag;

    protected PendulumAnimationWindMixin(Function<BlockState, Vector3f> axisGetter) {
        super(axisGetter);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void spicedcider$windTorque(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!level.isClientSide() || !ClientWind.active()) return;
        float strength = ClientWind.exposedStrength(pos);
        if (strength < 0.01F) return;
        Vector3f push = new Vector3f(getRotationAxis(state)).cross(spicedcider$UP);
        float facing = push.x() * ClientWind.directionX() + push.z() * ClientWind.directionZ();
        float time = ClientWind.time() * SECONDS_PER_TICK;
        float flutter = Mth.sin(time * 2.3F + pos.getX() * 0.7F + pos.getZ() * 1.3F) * Mth.sin(time * 0.9F + pos.getY());
        float exposure = strength / Math.max(0.01F, ClientWind.strengthAt(pos.getY() + 0.5));
        float torque = facing * strength * TORQUE_PER_STRENGTH + flutter * ClientWind.turbulence() * exposure * TURBULENCE_TORQUE;
        angularVel += 0.05F * torque * Mth.cos(angle);
        hasDrag = true;
    }
}
