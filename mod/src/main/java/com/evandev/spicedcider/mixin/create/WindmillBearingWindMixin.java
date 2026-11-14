package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.wind.Wind;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("create")
@Mixin(value = WindmillBearingBlockEntity.class, remap = false)
public abstract class WindmillBearingWindMixin extends MechanicalBearingBlockEntity {
    @Unique
    private static final int CHECK_INTERVAL = 40;
    @Unique
    private static final float MIN_FACTOR = 0.75F;
    @Unique
    private static final float MAX_FACTOR = 1.5F;
    @Unique
    private static final float FACTOR_STEP = 0.125F;
    @Unique
    private static final float FULL_STRENGTH = 1.6F;

    @Unique
    private float spicedcider$windFactor = 1.0F;

    protected WindmillBearingWindMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @WrapOperation(method = "getGeneratedSpeed", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/bearing/WindmillBearingBlockEntity;getAngleSpeedDirection()F"))
    private float spicedcider$windSpeed(WindmillBearingBlockEntity bearing, Operation<Float> original) {
        return original.call(bearing) * spicedcider$windFactor;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void spicedcider$followWind(CallbackInfo ci) {
        if (!(level instanceof ServerLevel server)) return;
        if (Math.floorMod(server.getGameTime() + worldPosition.asLong(), CHECK_INTERVAL) != 0) return;
        float target = 1.0F;
        if (Wind.enabled() && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.windCreateWindmills, true)) {
            float strength = Wind.sample(server, worldPosition).strength() / FULL_STRENGTH;
            float raw = Mth.lerp(Mth.clamp(strength, 0.0F, 1.0F), MIN_FACTOR, MAX_FACTOR);
            if (Math.abs(raw - spicedcider$windFactor) < FACTOR_STEP * 0.75F) return;
            target = Math.round(raw / FACTOR_STEP) * FACTOR_STEP;
        }
        if (target == spicedcider$windFactor) return;
        spicedcider$windFactor = target;
        if (running && movedContraption != null) updateGeneratedRotation();
    }
}
