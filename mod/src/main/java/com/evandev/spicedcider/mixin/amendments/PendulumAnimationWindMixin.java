package com.evandev.spicedcider.mixin.amendments;

import com.jvn.wherewindsblow.client.wind.DynamicWindManager;
import com.jvn.wherewindsblow.client.wind.WindSample;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@IfModLoaded("amendments")
@IfModLoaded("where_winds_blow")
@Mixin(value = PendulumAnimation.class, remap = false)
public abstract class PendulumAnimationWindMixin extends SwingAnimation {
    private static final Vector3f UP = new Vector3f(0.0F, 1.0F, 0.0F);
    private static final float TORQUE_PER_STRENGTH = 3.5F;
    private static final float TURBULENCE_TORQUE = 6.0F;

    @Shadow
    private float angularVel;

    @Shadow
    private boolean hasDrag;

    protected PendulumAnimationWindMixin(Function<BlockState, Vector3f> axisGetter) {
        super(axisGetter);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void spicedcider$windTorque(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!level.isClientSide()) return;
        WindSample wind = DynamicWindManager.sampleWind(level, pos);
        if (wind.strength() < 0.01F) return;
        Vector3f push = new Vector3f(getRotationAxis(state)).cross(UP);
        float facing = push.x() * wind.directionX() + push.z() * wind.directionZ();
        float time = DynamicWindManager.simulationTime();
        float flutter = Mth.sin(time * 2.3F + pos.getX() * 0.7F + pos.getZ() * 1.3F) * Mth.sin(time * 0.9F + pos.getY());
        float torque = facing * wind.strength() * TORQUE_PER_STRENGTH + flutter * wind.turbulence() * TURBULENCE_TORQUE;
        angularVel += 0.05F * torque * Mth.cos(angle);
        hasDrag = true;
    }
}
